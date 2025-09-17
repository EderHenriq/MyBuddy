document.addEventListener("DOMContentLoaded", () => {
  const signInBtn = document.querySelector(".sign-in");
  const signUpBtn = document.querySelector(".sign-up");
  const glider = document.querySelector(".glider");
  const signInForm = document.querySelector(".sign-in-form");
  const signUpForm = document.querySelector(".sign-up-form");

  signInBtn.addEventListener("click", () => {
    glider.style.transform = "translateX(0)";
    signInBtn.classList.add("active");
    signUpBtn.classList.remove("active");

    // mostra login, esconde registro
    signInForm.classList.add("active");
    signUpForm.classList.remove("active");
  });

  signUpBtn.addEventListener("click", () => {
    glider.style.transform = "translateX(100%)";
    signUpBtn.classList.add("active");
    signInBtn.classList.remove("active");

    // mostra registro, esconde login
    signUpForm.classList.add("active");
    signInForm.classList.remove("active");
  });
});
