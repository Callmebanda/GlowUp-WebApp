<<<<<<< HEAD
document.addEventListener('DOMContentLoaded', function() {
    // Featured salons data from your database
    const featuredSalons = [
        {
            salon_id: 1,
            name: "Luxury Hair Studio",
            description: "Premium hair services and spa treatments",
            address: "123 Beauty St",
            city: "Glamour City",
            phone: "555-0201",
            email: "luxury@example.com",
            owner_id: 8,
            image_url: "https://via.placeholder.com/300x200?text=Luxury+Hair+Studio",
            rating: 4.80,
            featured: 1
        },
        {
            salon_id: 2,
            name: "Urban Nails Lounge",
            description: "Specialized nail art and waxing services",
            address: "456 Style Ave",
            city: "Glamour City",
            phone: "555-0202",
            email: "urban@example.com",
            owner_id: 2,
            image_url: "https://via.placeholder.com/300x200?text=Urban+Nails+Lounge",
            rating: 4.90,
            featured: 1
        },
        {
            salon_id: 3,
            name: "Gentleman's Barbershop",
            description: "Classic barber services for men",
            address: "789 Groom Blvd",
            city: "Glamour City",
            phone: "555-0203",
            email: "gentleman@example.com",
            owner_id: 3,
            image_url: "https://via.placeholder.com/300x200?text=Gentleman's+Barbershop",
            rating: 4.70,
            featured: 1
        }
    ];

    // Display the featured salons
    displayFeaturedSalons(featuredSalons);
});

function displayFeaturedSalons(salons) {
    const salonGrid = document.getElementById('salonGrid');

    if (!salonGrid) {
        console.error('Salon grid element not found');
        return;
    }

    // Clear loading message
    salonGrid.innerHTML = '';

    if (!salons || salons.length === 0) {
        salonGrid.innerHTML = '<div class="no-salons">No featured salons found.</div>';
        return;
    }

    // Create and append salon cards
    salons.forEach(salon => {
        const card = document.createElement('div');
        card.className = 'salon-card';
        card.innerHTML = `
                <div class="salon-image" style="background-image: url('${salon.image_url || 'https://via.placeholder.com/300x200?text=Salon+Image'}')">
                    <span class="rating">${salon.rating || '4.5'} <i class="fas fa-star"></i></span>
                </div>
                <div class="salon-info">
                    <h3>${salon.name}</h3>
                    <p class="location"><i class="fas fa-map-marker-alt"></i> ${salon.city}</p>
                    <p class="services">${getServiceTypes(salon.salon_id)}</p>
                    <a href="salon-profile.html?salonId=${salon.salon_id}" class="btn-outline">View Salon</a>
                </div>
            `;
        salonGrid.appendChild(card);
    });
}

function getServiceTypes(salonId) {
    // Map salon IDs to their primary services
    const salonServices = {
        1: "Hair, Spa",       // Luxury Hair Studio
        2: "Nails, Waxing",   // Urban Nails Lounge
        3: "Barber, Shaves"   // Gentleman's Barbershop
    };
    return salonServices[salonId] || "Various Services";
}
=======
// Mobile Menu Toggle
document.addEventListener('DOMContentLoaded', function() {
    const mobileMenuToggle = document.querySelector('.mobile-menu-toggle');
    const mainNav = document.querySelector('.main-nav');
    const authButtons = document.querySelector('.auth-buttons');

    if (mobileMenuToggle) {
        mobileMenuToggle.addEventListener('click', function() {
            // Toggle mobile menu visibility
            const isNavVisible = mainNav.style.display === 'block';
            mainNav.style.display = isNavVisible ? 'none' : 'block';
            authButtons.style.display = isNavVisible ? 'none' : 'block';

            // Toggle menu icon
            const icon = mobileMenuToggle.querySelector('i');
            if (icon) {
                icon.className = isNavVisible ? 'fas fa-bars' : 'fas fa-times';
            }
        });
    }

    // Adjust for window resize
    window.addEventListener('resize', function() {
        if (window.innerWidth > 768) {
            // Reset to default styles on larger screens
            if (mainNav) mainNav.style.display = '';
            if (authButtons) authButtons.style.display = '';

            // Reset menu icon if exists
            const icon = mobileMenuToggle?.querySelector('i');
            if (icon) icon.className = 'fas fa-bars';
        } else {
            // Ensure mobile menu is hidden by default on small screens
            if (mainNav) mainNav.style.display = 'none';
            if (authButtons) authButtons.style.display = 'none';
        }
    });

    // Initialize mobile menu state
    if (window.innerWidth <= 768) {
        if (mainNav) mainNav.style.display = 'none';
        if (authButtons) authButtons.style.display = 'none';
    }
});
>>>>>>> 1fc0e8b4aead21815ee64711e16f9a850303b202
