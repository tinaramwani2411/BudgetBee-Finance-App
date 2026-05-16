// BudgetBee Analytics / Charts Module

function updateCharts(data) {
    renderTrendChart(data.monthlyTrend || []);
    renderCategoryChart(data.categorySummary || []);
}

function renderTrendChart(monthlyData) {
    const ctx = document.getElementById('trendChart');
    if (!ctx) return;
    if (chartInstances.trend) chartInstances.trend.destroy();

    const monthNames = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    const labels = monthlyData.map(d => monthNames[(d.month || d[0]) - 1] || '');
    const values = monthlyData.map(d => parseFloat(d.total || d[1] || 0));

    chartInstances.trend = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Monthly Expense',
                data: values,
                borderColor: '#FFC107',
                backgroundColor: 'rgba(255, 193, 7, 0.1)',
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#FFC107',
                pointRadius: 4,
                borderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: { legend: { display: false } },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: 'rgba(0,0,0,0.05)' },
                    ticks: { callback: v => 'Rs.' + v }
                },
                x: {
                    grid: { display: false }
                }
            }
        }
    });
}

function renderCategoryChart(categoryData) {
    const ctx = document.getElementById('categoryChart');
    if (!ctx) return;
    if (chartInstances.category) chartInstances.category.destroy();

    const colors = ['#FFC107', '#FFA000', '#FF6F00', '#F57C00', '#FFB74D',
                    '#FFD54F', '#FFE082', '#FFCC80', '#FFAB91'];
    const labels = categoryData.map(d => d.category || d[0] || '');
    const values = categoryData.map(d => parseFloat(d.total || d[1] || 0));

    chartInstances.category = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: colors.slice(0, labels.length),
                borderWidth: 2,
                borderColor: '#FFF'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 16,
                        usePointStyle: true,
                        font: { size: 11 }
                    }
                }
            },
            cutout: '60%'
        }
    });
}

async function downloadReport(format) {
    if (!API.isLoggedIn()) {
        showToast('Please login to download reports', 'error');
        return;
    }

    const monthInput = document.getElementById('reportMonth').value;
    if (!monthInput) {
        showToast('Please select a month', 'error');
        return;
    }

    const [year, month] = monthInput.split('-');
    const filename = `budgetbee_report_${year}_${month}.${format}`;

    try {
        await API.download(`/api/reports/${format}?year=${year}&month=${month}`, filename);
        showToast(`Report downloaded as ${filename}`, 'success');
    } catch (err) {
        showToast(err.message, 'error');
    }
}
