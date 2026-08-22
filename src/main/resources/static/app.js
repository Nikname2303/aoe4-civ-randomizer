/* =============================================================
   AoE4 Civ Randomizer — app.js
   Plain vanilla JS. No frameworks. Uses fetch() for all API calls.
   ============================================================= */

// ── On page load: fetch civs and render the list, add a default player row ──
document.addEventListener('DOMContentLoaded', () => {
    loadCivs();
    addPlayerRow();       // start with one empty row in the lobby table
});


// ══════════════════════════════════════════════════════════════════════════════
//  QUICK SOLO ROLL
// ══════════════════════════════════════════════════════════════════════════════

document.getElementById('solo-btn').addEventListener('click', async () => {
    const resultEl = document.getElementById('solo-result');
    resultEl.textContent = '…picking…';
    resultEl.className = 'result-text';

    try {
        const res = await fetch('/api/random/single', { method: 'POST' });
        if (!res.ok) {
            const err = await res.json();
            resultEl.textContent = '⚠ ' + (err.message || 'Error');
            resultEl.className = 'result-text error-text';
            return;
        }
        const civ = await res.json();
        resultEl.textContent = '🏰 ' + civ.name + '  (' + civ.dlc + ')';
    } catch (e) {
        resultEl.textContent = '⚠ Could not reach the server.';
        resultEl.className = 'result-text error-text';
    }
});


// ══════════════════════════════════════════════════════════════════════════════
//  LOBBY RANDOMIZER
// ══════════════════════════════════════════════════════════════════════════════

// ── Add a new empty player row ──
document.getElementById('add-player-btn').addEventListener('click', addPlayerRow);

// ── Remove the last player row ──
document.getElementById('remove-player-btn').addEventListener('click', () => {
    const tbody = document.getElementById('player-rows');
    if (tbody.rows.length > 1) {
        tbody.deleteRow(tbody.rows.length - 1);
    }
});

function addPlayerRow() {
    const tbody = document.getElementById('player-rows');
    const row = tbody.insertRow();

    // Left column: editable player name input
    const nameCell = row.insertCell(0);
    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = 'Player name';
    input.className = 'player-name-input';
    nameCell.appendChild(input);

    // Right column: assigned civ (empty until randomized)
    const civCell = row.insertCell(1);
    civCell.textContent = '—';
    civCell.className = 'assigned-civ';
}

// ── Randomize lobby ──
document.getElementById('lobby-btn').addEventListener('click', async () => {
    const errorEl = document.getElementById('lobby-error');
    errorEl.classList.add('hidden');

    // Collect non-empty player names from the table
    const inputs = document.querySelectorAll('.player-name-input');
    const playerNames = Array.from(inputs)
        .map(i => i.value.trim())
        .filter(n => n.length > 0);

    if (playerNames.length === 0) {
        showLobbyError('Please enter at least one player name.');
        return;
    }

    const allowDuplicates = document.getElementById('allow-duplicates-checkbox').checked;

    try {
        const res = await fetch('/api/random/lobby', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ playerNames, allowDuplicates })
        });

        if (!res.ok) {
            const err = await res.json();
            showLobbyError(err.message || 'An error occurred.');
            return;
        }

        // Result is { "Alice": "English", "Bob": "Mongols", ... }
        const assignments = await res.json();

        // Write the assigned civ into the right column of each row
        const rows = document.querySelectorAll('#player-rows tr');
        rows.forEach(row => {
            const nameInput = row.querySelector('.player-name-input');
            const civCell = row.querySelector('.assigned-civ');
            if (nameInput && civCell) {
                const name = nameInput.value.trim();
                civCell.textContent = assignments[name] || '—';
            }
        });
    } catch (e) {
        showLobbyError('Could not reach the server.');
    }
});

function showLobbyError(msg) {
    const el = document.getElementById('lobby-error');
    el.textContent = '⚠ ' + msg;
    el.classList.remove('hidden');
}


// ══════════════════════════════════════════════════════════════════════════════
//  CIV SELECTION PANEL
// ══════════════════════════════════════════════════════════════════════════════

async function loadCivs() {
    const container = document.getElementById('civ-list');
    container.textContent = 'Loading…';

    try {
        const res = await fetch('/api/civs');
        const civs = await res.json();
        renderCivList(civs);
    } catch (e) {
        container.textContent = 'Could not load civilizations. Is the server running?';
    }
}

function renderCivList(civs) {
    const container = document.getElementById('civ-list');
    container.innerHTML = '';

    // Group civs by DLC name
    const groups = {};
    civs.forEach(civ => {
        if (!groups[civ.dlc]) groups[civ.dlc] = [];
        groups[civ.dlc].push(civ);
    });

    // Render each group as a subsection with a heading
    Object.keys(groups).sort().forEach(dlcName => {
        const groupEl = document.createElement('div');
        groupEl.className = 'civ-group';

        const heading = document.createElement('h3');
        heading.textContent = dlcName;
        groupEl.appendChild(heading);

        groups[dlcName].forEach(civ => {
            const label = document.createElement('label');
            label.className = 'civ-item';

            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.checked = civ.enabled;
            checkbox.dataset.civId = civ.id;

            // Toggling a checkbox immediately persists via the API
            checkbox.addEventListener('change', () => toggleCiv(civ.id, checkbox));

            label.appendChild(checkbox);
            label.appendChild(document.createTextNode(' ' + civ.name));
            groupEl.appendChild(label);
        });

        container.appendChild(groupEl);
    });
}

async function toggleCiv(id, checkbox) {
    // Optimistically update the UI, then confirm with the server
    try {
        const res = await fetch('/api/civs/' + id + '/toggle', { method: 'POST' });
        if (!res.ok) {
            // Revert the checkbox if the server call failed
            checkbox.checked = !checkbox.checked;
            alert('Could not save the change. Please try again.');
        }
        // On success the server returns the updated civ — we trust the checkbox state we already set
    } catch (e) {
        checkbox.checked = !checkbox.checked;
        alert('Could not reach the server.');
    }
}
