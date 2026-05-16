// BudgetBee Expenses Module

let editingExpenseId = null;

function setupExpenseDate() {
    const d = new Date();
    document.getElementById('expDate').value = d.toISOString().split('T')[0];
}

async function loadExpenses() {
    const container = document.getElementById('expenseItems');
    const isLoggedIn = API.isLoggedIn();

    if (!isLoggedIn) {
        container.innerHTML = `
            <div class="empty-state">
                🐝 Login to see and manage your expenses!<br>
                <small style="color:var(--gray-light);margin-top:8px;display:block">
                    Sign in to start tracking your personal expenses.
                </small>
            </div>`;
        return;
    }

    const category = document.getElementById('filterCategory').value;
    const searchQ = document.getElementById('searchInput').value.trim();

    try {
        let url = searchQ
            ? `/api/expenses/search?q=${encodeURIComponent(searchQ)}`
            : `/api/expenses?year=${currentYear}&month=${currentMonth}`;

        if (!searchQ && category) url += `&category=${encodeURIComponent(category)}`;

        const expenses = await API.get(url);

        if (!expenses || expenses.length === 0) {
            container.innerHTML = `<div class="empty-state">No expenses found for this period.</div>`;
            return;
        }

        container.innerHTML = '';
        expenses.forEach(exp => {
            const icons = {
                'Food': '🍔', 'Transport': '🚗', 'Shopping': '🛍️',
                'Bills': '💡', 'Entertainment': '🎬', 'Health': '💊',
                'Education': '📚', 'Rent': '🏠', 'Other': '📌'
            };
            const icon = icons[exp.category] || '📌';
            const date = new Date(exp.date + 'T00:00:00').toLocaleDateString('en-IN', {
                day: 'numeric', month: 'short', year: 'numeric'
            });

            const item = document.createElement('div');
            item.className = 'expense-item';
            item.innerHTML = `
                <div class="exp-category">${icon}</div>
                <div class="exp-details">
                    <span class="exp-title">${escapeHtml(exp.title)}</span>
                    <span class="exp-meta">${exp.category} · ${date}</span>
                </div>
                <div class="exp-amount">Rs.${formatCurrency(exp.amount)}</div>
                <div class="exp-actions">
                    <button onclick="editExpense(${exp.id})" title="Edit">✏️</button>
                    <button onclick="deleteExpense(${exp.id})" title="Delete">🗑️</button>
                </div>
            `;
            container.appendChild(item);
        });
    } catch (err) {
        showToast('Error loading expenses: ' + err.message, 'error');
    }
}

async function handleAddExpense(e) {
    e.preventDefault();
    if (!API.isLoggedIn()) {
        showToast('Please login to add expenses', 'error');
        return;
    }

    const title = document.getElementById('expTitle').value;
    const amount = document.getElementById('expAmount').value;
    const category = document.getElementById('expCategory').value;
    const date = document.getElementById('expDate').value;
    const description = document.getElementById('expDescription').value;

    if (!title || !amount || !category || !date) {
        showToast('Please fill all required fields', 'error');
        return;
    }

    try {
        if (editingExpenseId) {
            await API.put('/api/expenses/' + editingExpenseId, {
                title, amount: parseFloat(amount), category, date, description
            });
            showToast('Expense updated successfully!', 'success');
            cancelEdit();
        } else {
            await API.post('/api/expenses', {
                title, amount: parseFloat(amount), category, date, description
            });
            showToast('Expense added successfully!', 'success');
        }

        document.getElementById('expenseForm').reset();
        setupExpenseDate();
        loadDashboard();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function editExpense(id) {
    if (!API.isLoggedIn()) return;
    editingExpenseId = id;

    const addBtn = document.getElementById('addExpBtn');
    addBtn.textContent = 'Update Expense';
    addBtn.style.background = 'var(--green)';
    document.getElementById('cancelEditBtn').style.display = 'block';

    try {
        const expenses = await API.get(`/api/expenses?year=${currentYear}&month=${currentMonth}`);
        const exp = expenses.find(e => e.id === id);
        if (!exp) return;

        document.getElementById('expTitle').value = exp.title;
        document.getElementById('expAmount').value = exp.amount;
        document.getElementById('expCategory').value = exp.category;
        document.getElementById('expDate').value = exp.date;
        document.getElementById('expDescription').value = exp.description || '';
        document.getElementById('expenseForm').scrollIntoView({ behavior: 'smooth' });
    } catch (err) {
        showToast('Error loading expense', 'error');
    }
}

function cancelEdit() {
    editingExpenseId = null;
    const addBtn = document.getElementById('addExpBtn');
    addBtn.textContent = 'Add Expense';
    addBtn.style.background = '';
    document.getElementById('cancelEditBtn').style.display = 'none';
    document.getElementById('expenseForm').reset();
    setupExpenseDate();
}

async function deleteExpense(id) {
    if (!confirm('Delete this expense?')) return;
    try {
        await API.del('/api/expenses/' + id);
        showToast('Expense deleted', 'success');
        loadDashboard();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function searchExpenses(e) {
    const q = e.target.value;
    if (q.length >= 2) {
        loadExpenses();
    } else if (q.length === 0) {
        loadExpenses();
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
