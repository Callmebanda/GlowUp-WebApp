document.addEventListener("DOMContentLoaded", function () {
  const buttons = document.querySelectorAll(".view-details");

  buttons.forEach(button => {
    button.addEventListener("click", function (e) {
      e.preventDefault(); // Stop the default link behavior
      const salonId = this.getAttribute("data-salon");

      // Redirect to the correct details page
      window.location.href = `salon-profiles/${salonId}.html`;
    });
  });
});
