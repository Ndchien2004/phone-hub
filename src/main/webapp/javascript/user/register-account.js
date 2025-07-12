const contextPath = document.body.getAttribute("data-context-path");

// Toggle password visibility
function toggleVisibility(icon) {
    const input = icon.previousElementSibling;
    if (input.type === "password") {
        input.type = "text";
        icon.src = "https://cdn-icons-png.flaticon.com/512/565/565655.png";
    } else {
        input.type = "password";
        icon.src = "https://cdn-icons-png.flaticon.com/512/709/709612.png";
    }
}

// -------------------- REGISTER FLOW --------------------

const registerForm = document.getElementById("registerForm");

registerForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    // Clear old errors
    document.querySelectorAll(".error-message").forEach(el => el.textContent = "");

    const formData = new FormData(registerForm);
    const params = new URLSearchParams();
    for (const [key, value] of formData.entries()) {
        params.append(key, value);
    }

    const response = await fetch(`${contextPath}/register-with-email`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params.toString()
    });

    const data = await response.json();

    if (!data.success) {
        if (data.errors) {
            Object.entries(data.errors).forEach(([key, message]) => {
                const errorEl = document.getElementById(`${key}Error`);
                if (errorEl) errorEl.textContent = message;
            });
        } else if (data.error) {
            alert(data.error);
        }
        return;
    }

    // Nếu không có lỗi → gọi gửi OTP
    sendOtpForRegister(formData.get("email"));
});

function sendOtpForRegister(email) {
    const verifyBtn = document.getElementById("verifyOtpBtn");
    verifyBtn.disabled = true;
    verifyBtn.textContent = "Sending OTP...";

    fetch(`${contextPath}/send-otp`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `otpPurpose=register&email=${encodeURIComponent(email)}`
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                openOtpModal();
            } else {
                alert(data.error || "Failed to send OTP.");
            }
        })
        .catch(err => {
            console.error("Error sending OTP:", err);
            alert("Error sending OTP.");
        })
        .finally(() => {
            verifyBtn.disabled = false;
            verifyBtn.textContent = "Confirm";
        });
}

// -------------------- OTP MODAL --------------------

const otpModal = document.getElementById("otpModal");
const otpTimer = document.getElementById("otpTimer");
const otpCountdown = document.getElementById("otpCountdown");
const resendOtpLink = document.getElementById("resendOtpLink");
const otpError = document.getElementById("otpError");
let otpInterval;
let otpTimeLeft = 60;

function openOtpModal() {
    clearInterval(otpInterval);
    otpModal.classList.remove("hidden");

    otpError.textContent = "";
    otpError.classList.add("hidden");
    otpCountdown.classList.add("hidden");
    resendOtpLink.classList.add("hidden");

    document.querySelectorAll(".otp-input").forEach(input => input.value = "");
    document.querySelector(".otp-input")?.focus();

    startOtpCountdown();
}

function startOtpCountdown() {
    otpTimeLeft = 60;
    otpTimer.textContent = otpTimeLeft;
    resendOtpLink.classList.add("hidden");
    otpCountdown.classList.remove("hidden");

    otpInterval = setInterval(() => {
        otpTimeLeft--;
        otpTimer.textContent = otpTimeLeft;

        if (otpTimeLeft <= 0) {
            clearInterval(otpInterval);
            otpCountdown.classList.add("hidden");
            resendOtpLink.classList.remove("hidden");
        }
    }, 1000);
}

resendOtpLink.addEventListener("click", function () {
    resendOtpLink.classList.add("hidden");
    document.querySelectorAll(".otp-input").forEach(input => input.value = "");
    document.querySelector(".otp-input")?.focus();

    const email = document.getElementById("email")?.value;
    if (email) {
        sendOtpForRegister(email);
    }
});

// Tự động chuyển ô khi nhập OTP
document.querySelectorAll(".otp-input").forEach((input, idx, arr) => {
    input.addEventListener("input", () => {
        if (input.value.length === 1 && idx < arr.length - 1) {
            arr[idx + 1].focus();
        }
    });
    input.addEventListener("keydown", (e) => {
        if (e.key === "Backspace" && !input.value && idx > 0) {
            arr[idx - 1].focus();
        }
    });
});

function submitOtp() {
    const otpInputs = document.querySelectorAll(".otp-input");
    const verifyBtn = document.getElementById("verifyOtpBtn");

    otpError.textContent = "";
    otpError.classList.add("hidden");

    verifyBtn.disabled = true;
    verifyBtn.innerHTML = `<svg class="inline w-4 h-4 mr-2 animate-spin text-white" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"></path>
      </svg>Verifying...`;

    const formData = new URLSearchParams();
    otpInputs.forEach((input, i) => formData.append(`d${i + 1}`, input.value.trim()));

    fetch(`${contextPath}/verify-otp`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData.toString()
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                // ✅ Dùng redirect trả về từ server
                window.location.href = data.redirect;
            } else {
                otpError.textContent = data.message || "Invalid code.";
                otpError.classList.remove("hidden");
                document.querySelectorAll(".otp-input").forEach(input => input.value = "");
                document.querySelector(".otp-input")?.focus();
            }
        })
        .catch(() => {
            otpError.textContent = "An error occurred. Please try again.";
            otpError.classList.remove("hidden");
        })
        .finally(() => {
            verifyBtn.disabled = false;
            verifyBtn.textContent = "Confirm";
        });
}
