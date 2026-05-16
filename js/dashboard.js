// BudgetBee Dashboard Module

let currentYear = new Date().getFullYear();
let currentMonth = new Date().getMonth() + 1;
let chartInstances = {};

function changeMonth(delta) {
    currentMonth += delta;
    if (currentMonth < 1) { currentMonth = 12; currentYear--; }
    if (currentMonth > 12) { currentMonth = 1; currentYear++; }
    loadDashboard();
}

async function loadDashboard() {
    const isLoggedIn = API.isLoggedIn();
    const monthDisplay = document.getElementById('currentMonthDisplay');
    const monthNames = ['January','February','March','April','May','June',
                        'July','August','September','October','November','December'];
    monthDisplay.textContent = monthNames[currentMonth - 1] + ' ' + currentYear;

    try {
        let data;
        if (isLoggedIn) {
            data = await API.get(`/api/dashboard?year=${currentYear}&month=${currentMonth}`);
        } else {
            data = await API.get('/api/dashboard/public');
        }

        updateSummaryCards(data);
        updateBudget(data);
        loadExpenses();
        updateCharts(data);
    } catch (err) {
        showToast('Error loading dashboard: ' + err.message, 'error');
    }
}

function updateSummaryCards(data) {
    document.getElementById('totalSpent').textContent = 'Rs.' + formatCurrency(data.totalExpense || 0);
    document.getElementById('lastExpense').textContent = 'Rs.' + formatCurrency(data.lastExpense || 0);
    document.getElementById('expenseCount').textContent = data.expenseCount || 0;

    const remaining = data.remainingBudget || 0;
    const remEl = document.getElementById('remainingBudget');
    remEl.textContent = 'Rs.' + formatCurrency(remaining);
    remEl.style.color = remaining < 0 ? 'var(--red)' : 'inherit';
}

function updateBudget(data) {
    const budget = data.monthlyBudget || 0;
    const total = data.totalExpense || 0;
    const progressEl = document.getElementById('budgetProgress');
    const alertEl = document.getElementById('budgetAlert');

    if (budget > 0) {
        progressEl.style.display = 'block';
        const pct = Math.min(100, (total / budget) * 100);
        const fill = document.getElementById('progressFill');
        fill.style.width = pct + '%';
        fill.className = 'progress-fill' + (pct > 80 ? ' warning' : '');
        document.getElementById('progressText').textContent = pct.toFixed(0) + '% used';

        if (total > budget) {
            alertEl.style.display = 'block';
        } else {
            alertEl.style.display = 'none';
        }
    } else {
        progressEl.style.display = 'none';
        alertEl.style.display = 'none';
    }
}

async function setMonthlyBudget() {
    if (!API.isLoggedIn()) {
        showToast('Please login to set a budget', 'error');
        return;
    }

    const amount = document.getElementById('budgetInput').value;
    if (!amount || amount <= 0) {
        showToast('Enter a valid budget amount', 'error');
        return;
    }

    try {
        await API.post('/api/dashboard/budget', {
            amount: parseFloat(amount),
            year: currentYear,
            month: currentMonth
        });
        showToast('Budget set to Rs.' + formatCurrency(amount), 'success');
        document.getElementById('budgetInput').value = '';
        loadDashboard();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// Toast notification
function showToast(message, type) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = 'toast ' + type + ' show';
    clearTimeout(toast._timer);
    toast._timer = setTimeout(() => toast.classList.remove('show'), 3000);
}

function formatCurrency(val) {
    return Number(val).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}
