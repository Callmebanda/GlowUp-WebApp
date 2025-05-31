document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadSalons();

    document.getElementById('searchBtn').addEventListener('click', loadSalons);
});

function loadCategories() {
    fetch('/api/categories')
        .then(response => response.json())
        .then(categories => {
            const select = document.getElementById('categoryFilter');
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.textContent = category.name;
                select.appendChild(option);
            });
        });
}

function loadSalons() {
    const location = document.getElementById('locationSearch').value;
    const category = document.getElementById('categoryFilter').value;
    const rating = document.getElementById('ratingFilter').value;

    let url = '/api/salons?';
    if (location) url += `location=${encodeURIComponent(location)}&`;
    if (category) url += `category=${category}&`;
    if (rating > 0) url += `minRating=${rating}&`;

    fetch(url)
        .then(response => response.json())
        .then(salons => {
            const grid = document.getElementById('salonGrid');
            grid.innerHTML = salons.map(salon => `
                <div class="salon-card" data-id="${salon.id}">
                    <div class="salon-image" style="background-image: url('${salon.imageUrl || '/images/default-salon.jpg'}')"></div>
                    <div class="salon-info">
                        <h3>${salon.name}</h3>
                        <p class="location">${salon.city}</p>
                        <div class="rating">${renderStars(salon.rating)} (${salon.reviewCount || 0})</div>
                        <a href="/salon-profile.html?id=${salon.id}" class="btn-primary">View Salon</a>
                    </div>
                </div>
            `).join('');
        });
}

function renderStars(rating) {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5 ? 1 : 0;
    const emptyStars = 5 - fullStars - halfStar;

    return '★'.repeat(fullStars) +
        (halfStar ? '½' : '') +
        '☆'.repeat(emptyStars);
}