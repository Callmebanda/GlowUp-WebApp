const contextPath = '/glowup'; // ✅ Replace this with your actual app name from Tomcat
const url = `${window.location.origin}${contextPath}/services?salonId=1`;

fetch(url)
    .then(response => {
        if (!response.ok) throw new Error("Failed to load services");
        return response.json();
    })
    .then(data => {
        console.log("Services received:", data);

    })
    .catch(error => {
        console.error("Error loading services:", error);
    });

document.addEventListener('DOMContentLoaded', function() {
    // Get salon ID from URL (in a real app, this would come from the previous page)
    const urlParams = new URLSearchParams(window.location.search);
    const salonId = urlParams.get('salonId') || 1; // Default to 1 for demo

    // Load salon info
    loadSalonInfo(salonId);

    // Load services
    loadServices(salonId);

    // Set up filter button
    document.getElementById('apply-filters').addEventListener('click', function() {
        const category = document.getElementById('category-filter').value;
        const gender = document.getElementById('gender-filter').value;
        const price = document.getElementById('price-filter').value;

        loadServices(salonId, category, gender, price);
    });

    // Set up service modal
    const serviceModal = document.getElementById('service-modal');
    const closeModal = document.querySelector('.close-modal');
    const bookServiceBtn = document.getElementById('book-service-btn');

    closeModal.addEventListener('click', function() {
        serviceModal.classList.remove('active');
    });

    bookServiceBtn.addEventListener('click', function() {
        const serviceId = this.getAttribute('data-service-id');
        window.location.href = `booking.html?salonId=${salonId}&serviceId=${serviceId}`;
    });

    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === serviceModal) {
            serviceModal.classList.remove('active');
        }
    });
});

function loadSalonInfo(salonId) {
    // In a real app, this would fetch from the server
    document.getElementById('salon-name').textContent = "GlowUp Salon & Spa";
    document.getElementById('salon-address').textContent = "123 Beauty Street, Glamour City";
}

function loadServices(salonId, category = '', gender = '', price = '') {
    const servicesContainer = document.getElementById('services-container');
    servicesContainer.innerHTML = '<div class="loading">Loading services...</div>';

    // Build query parameters
    let params = `salonId=${salonId}`;
    if (category) params += `&category=${category}`;
    if (gender) params += `&gender=${gender}`;

    fetch(`/services?${params}`)
        .then(response => response.json())
        .then(services => {
            // Apply price filter client-side for demo
            if (price) {
                const [min, max] = price.split('-').map(Number);
                services = services.filter(service => {
                    const servicePrice = service.price;
                    if (isNaN(min)) return servicePrice >= max;
                    if (isNaN(max)) return servicePrice <= min;
                    return servicePrice >= min && servicePrice <= max;
                });
            }

            if (services.length === 0) {
                servicesContainer.innerHTML = '<div class="no-results">No services found matching your criteria.</div>';
                return;
            }

            servicesContainer.innerHTML = '';

            services.forEach(service => {
                const serviceCard = document.createElement('div');
                serviceCard.className = 'service-card';
                serviceCard.innerHTML = `
                    <div class="service-image" style="background-image: url('https://source.unsplash.com/random/300x200/?${service.category}')"></div>
                    <div class="service-content">
                        <h3>${service.name}</h3>
                        <div class="service-meta">
                            <span class="service-duration">${service.duration} mins</span>
                            <span class="service-price">$${service.price.toFixed(2)}</span>
                        </div>
                        <p class="service-description">${service.description || 'No description available.'}</p>
                        <div class="service-actions">
                            <button class="btn view-details" data-service-id="${service.serviceId}">View Details</button>
                        </div>
                    </div>
                `;

                servicesContainer.appendChild(serviceCard);

                // Add click event to view details button
                serviceCard.querySelector('.view-details').addEventListener('click', function() {
                    openServiceModal(service);
                });
            });
        })
        .catch(error => {
            console.error('Error loading services:', error);
            servicesContainer.innerHTML = '<div class="error">Failed to load services. Please try again later.</div>';
        });
}

function openServiceModal(service) {
    const modal = document.getElementById('service-modal');

    // Populate modal with service details
    document.getElementById('modal-service-name').textContent = service.name;
    document.getElementById('modal-service-category').textContent = service.category.charAt(0).toUpperCase() + service.category.slice(1);
    document.getElementById('modal-service-gender').textContent = service.gender === 'men' ? 'Men' : service.gender === 'women' ? 'Women' : 'Unisex';
    document.getElementById('modal-service-duration').textContent = `${service.duration} minutes`;
    document.getElementById('modal-service-price').textContent = `$${service.price.toFixed(2)}`;
    document.getElementById('modal-service-description').textContent = service.description || 'No description available.';

    // Set service ID on book button
    document.getElementById('book-service-btn').setAttribute('data-service-id', service.serviceId);

    // Show modal
    modal.classList.add('active');
}