/* =============================================================
   AoE4 Civ Randomizer — app.js
   Plain vanilla JS. No frameworks. Uses the JavaFX bridge for all backend calls.
   ============================================================= */

const civByName = {};
const GENERIC_CIV_ICON_PATH = '/images/civs/generic.png';
let genericIconDataUri = '';
let appInitialized = false;

window.appInit = function appInit() {
    if (appInitialized) {
        return;
    }
    appInitialized = true;
    initializeApp();
};

function initializeApp() {
    loadGenericIcon();
    loadCivs();
    addPlayerRow();
}

function bridgeCallJson(methodName, ...args) {
    if (!window.javaBridge) {
        throw new Error('Desktop bridge is not available.');
    }

    let raw;
    switch (methodName) {
        case 'getCivs':
            raw = window.javaBridge.getCivs();
            break;
        case 'getGenericIcon':
            raw = window.javaBridge.getGenericIcon();
            break;
        case 'randomSingle':
            raw = window.javaBridge.randomSingle();
            break;
        case 'randomLobby':
            raw = window.javaBridge.randomLobby(args[0]);
            break;
        case 'toggleCiv':
            raw = window.javaBridge.toggleCiv(args[0]);
            break;
        case 'setDlcEnabled':
            raw = window.javaBridge.setDlcEnabled(args[0], args[1]);
            break;
        default:
            throw new Error('Unsupported bridge method: ' + methodName);
    }

    const parsed = JSON.parse(raw);
    if (parsed && parsed.__bridgeError === true) {
        throw new Error(parsed.message || 'Error');
    }
    return parsed;
}

function loadGenericIcon() {
    try {
        const result = bridgeCallJson('getGenericIcon');
        genericIconDataUri = result.iconDataUri || '';
    } catch (e) {
        genericIconDataUri = '';
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  QUICK SOLO ROLL
// ══════════════════════════════════════════════════════════════════════════════

document.getElementById('solo-btn').addEventListener('click', async () => {
    const resultEl = document.getElementById('solo-result');
    resultEl.innerHTML = '…picking…';
    resultEl.className = 'solo-result-area';

    try {
        const civ = await bridgeCallJson('randomSingle');
        resultEl.innerHTML = '';
        resultEl.appendChild(createCivInline(civ, true));
    } catch (e) {
        resultEl.textContent = '⚠ ' + (e.message || 'Error');
        resultEl.classList.add('error-text');
    }
});

// ══════════════════════════════════════════════════════════════════════════════
//  LOBBY RANDOMIZER
// ══════════════════════════════════════════════════════════════════════════════

document.getElementById('add-player-btn').addEventListener('click', addPlayerRow);

document.getElementById('remove-player-btn').addEventListener('click', () => {
    const tbody = document.getElementById('player-rows');
    if (tbody.rows.length > 1) {
        tbody.deleteRow(tbody.rows.length - 1);
    }
});

function addPlayerRow() {
    const tbody = document.getElementById('player-rows');
    const row = tbody.insertRow();

    const nameCell = row.insertCell(0);
    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = 'Player name';
    input.className = 'player-name-input';
    nameCell.appendChild(input);

    const civCell = row.insertCell(1);
    civCell.textContent = '—';
    civCell.className = 'assigned-civ';
}

document.getElementById('lobby-btn').addEventListener('click', async () => {
    const errorEl = document.getElementById('lobby-error');
    errorEl.classList.add('hidden');

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
        const assignments = await bridgeCallJson('randomLobby', JSON.stringify({ playerNames, allowDuplicates }));
        const rows = document.querySelectorAll('#player-rows tr');
        rows.forEach(row => {
            const nameInput = row.querySelector('.player-name-input');
            const civCell = row.querySelector('.assigned-civ');
            if (nameInput && civCell) {
                const name = nameInput.value.trim();
                civCell.innerHTML = '';

                if (!name || !assignments[name]) {
                    civCell.textContent = '—';
                    return;
                }

                const civName = assignments[name];
                const civ = civByName[civName] || { name: civName };
                civCell.appendChild(createCivInline(civ));
            }
        });
    } catch (e) {
        showLobbyError(e.message || 'An error occurred.');
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
        const civs = await bridgeCallJson('getCivs');
        civs.forEach(civ => {
            civByName[civ.name] = civ;
        });
        renderCivList(civs);
    } catch (e) {
        container.textContent = 'Could not load civilizations.';
    }
}

function renderCivList(civs) {
    const container = document.getElementById('civ-list');
    container.innerHTML = '';

    const groups = {};
    civs.forEach(civ => {
        if (!groups[civ.dlc]) groups[civ.dlc] = [];
        groups[civ.dlc].push(civ);
    });

    Object.keys(groups).sort().forEach(dlcName => {
        const groupEl = document.createElement('div');
        groupEl.className = 'civ-group';

        const headingRow = document.createElement('div');
        headingRow.className = 'dlc-heading-row';

        const heading = document.createElement('h3');
        heading.textContent = dlcName;
        headingRow.appendChild(heading);

        const toggleLabel = document.createElement('label');
        toggleLabel.className = 'dlc-toggle-label';

        const toggleCheckbox = document.createElement('input');
        toggleCheckbox.type = 'checkbox';

        const allEnabled = groups[dlcName].every(c => c.enabled);
        const noneEnabled = groups[dlcName].every(c => !c.enabled);
        toggleCheckbox.checked = allEnabled;
        if (!allEnabled && !noneEnabled) {
            toggleCheckbox.indeterminate = true;
        }

        toggleLabel.appendChild(toggleCheckbox);
        toggleLabel.appendChild(document.createTextNode(' Toggle all'));
        headingRow.appendChild(toggleLabel);
        groupEl.appendChild(headingRow);

        const civCheckboxes = [];
        groups[dlcName].forEach(civ => {
            const label = document.createElement('label');
            label.className = 'civ-item';

            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.checked = civ.enabled;
            checkbox.dataset.civId = civ.id;
            civCheckboxes.push(checkbox);

            checkbox.addEventListener('change', () => {
                toggleCiv(civ.id, checkbox).then(() => {
                    updateDlcToggleState(toggleCheckbox, civCheckboxes);
                });
            });

            label.appendChild(checkbox);
            label.appendChild(createCivInline(civ));
            groupEl.appendChild(label);
        });

        toggleCheckbox.addEventListener('change', async () => {
            const newEnabled = toggleCheckbox.checked;
            toggleCheckbox.indeterminate = false;
            try {
                const updated = await bridgeCallJson('setDlcEnabled', dlcName, newEnabled);
                updated.forEach(updatedCiv => {
                    const cb = civCheckboxes.find(c => String(c.dataset.civId) === String(updatedCiv.id));
                    if (cb) cb.checked = updatedCiv.enabled;
                    civByName[updatedCiv.name] = updatedCiv;
                });
                updateDlcToggleState(toggleCheckbox, civCheckboxes);
            } catch (e) {
                toggleCheckbox.checked = !newEnabled;
                toggleCheckbox.indeterminate = false;
                alert('Could not save the change. Please try again.');
            }
        });

        container.appendChild(groupEl);
    });
}

function updateDlcToggleState(toggleCheckbox, civCheckboxes) {
    const allOn = civCheckboxes.every(cb => cb.checked);
    const allOff = civCheckboxes.every(cb => !cb.checked);
    toggleCheckbox.checked = allOn;
    toggleCheckbox.indeterminate = !allOn && !allOff;
}

async function toggleCiv(id, checkbox) {
    try {
        const updatedCiv = await bridgeCallJson('toggleCiv', id);
        checkbox.checked = updatedCiv.enabled;
        civByName[updatedCiv.name] = updatedCiv;
    } catch (e) {
        checkbox.checked = !checkbox.checked;
        alert('Could not save the change. Please try again.');
    }
}

function createCivInline(civ, largeIcon) {
    const civName = civ.name || '';
    const wrapper = document.createElement('span');
    wrapper.className = 'civ-inline';

    const img = document.createElement('img');
    img.className = largeIcon ? 'civ-icon-large' : 'civ-icon';
    img.alt = '';
    img.decoding = 'async';
    img.loading = largeIcon ? 'eager' : 'lazy';

    if (civ.iconDataUri) {
        img.src = civ.iconDataUri;
    } else if (genericIconDataUri) {
        img.src = genericIconDataUri;
    } else {
        img.src = resolveAbsoluteResourceUrl(civ.iconPath || GENERIC_CIV_ICON_PATH);
    }

    img.onerror = () => {
        console.error('Icon failed to load for "' + civName + '": ' + img.src);
        if (img.dataset.fallbackApplied === 'true') {
            img.style.display = 'none';
            return;
        }

        img.dataset.fallbackApplied = 'true';
        if (genericIconDataUri) {
            img.src = genericIconDataUri;
        } else {
            img.src = resolveAbsoluteResourceUrl(GENERIC_CIV_ICON_PATH);
        }
    };

    const text = document.createElement('span');
    text.textContent = civName;

    wrapper.appendChild(text);
    wrapper.appendChild(img);
    return wrapper;
}

function resolveAbsoluteResourceUrl(iconPath) {
    const relativePath = (iconPath || GENERIC_CIV_ICON_PATH).replace(/^\//, '');
    const baseUrl = window.appBaseUrl || window.location.href;
    return new URL(relativePath, baseUrl).toString();
}
