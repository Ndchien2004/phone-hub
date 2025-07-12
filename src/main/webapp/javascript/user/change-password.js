// Toggle password visibility
function toggleVisibility(icon) {
    const input = icon.previousElementSibling;
    if (input.type === "password") {
        input.type = "text";
        icon.src = "https://cdn-icons-png.flaticon.com/512/565/565655.png"; // open eye
    } else {
        input.type = "password";
        icon.src = "https://cdn-icons-png.flaticon.com/512/709/709612.png"; // closed eye
    }
}

// Show password rules when focusing on new password
const newPasswordInput = document.getElementById('newPassword');
const passwordRules = document.getElementById('passwordRules');

newPasswordInput.addEventListener('focus', () => {
    passwordRules.classList.remove('hidden');
});

newPasswordInput.addEventListener('blur', () => {
    passwordRules.classList.add('hidden');
});

// ----------------------
const contextPath = document.body.getAttribute("data-context-path");

const otpModal = document.getElementById("otpModal");
const otpTimer = document.getElementById("otpTimer");
const otpCountdown = document.getElementById("otpCountdown");
const resendOtpLink = document.getElementById("resendOtpLink");

let otpInterval;
let otpTimeLeft = 60;

function openOtpModal() {
    clearInterval(otpInterval);

    const verifyBtn = document.getElementById("verifyOtpBtn");

    otpModal.classList.remove("hidden");
    otpError.textContent = "";
    otpError.classList.add("hidden");
    otpCountdown.classList.add("hidden");
    resendOtpLink.classList.add("hidden");

    document.querySelectorAll(".otp-input").forEach(input => input.value = "");
    document.querySelector(".otp-input")?.focus();

    // Disable nút xác nhận trong lúc gửi OTP
    verifyBtn.disabled = true;
    verifyBtn.textContent = "Sending OTP...";

    sendOtp().then(() => {
        verifyBtn.disabled = false;
        verifyBtn.textContent = "Confirm";
        otpCountdown.classList.add("hidden");
        resendOtpLink.classList.remove("hidden");
        startOtpCountdown();
    }).catch(() => {
        otpError.textContent = "Failed to send OTP. Please try again.";
        document.querySelectorAll(".otp-input").forEach(input => input.value = "");
        document.querySelector(".otp-input")?.focus();
        otpError.classList.remove("hidden");
        verifyBtn.disabled = false;
        verifyBtn.textContent = "Confirm";
    });

}

function closeOtpModal() {
    otpModal.classList.add("hidden");
    clearInterval(otpInterval);
}

function sendOtp() {
    return fetch(`${contextPath}/send-otp`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `otpPurpose=resetPassword`
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                console.log("✅ OTP sent for forgot password (not logged in).");
            } else {
                alert("❌ Failed to send OTP: " + data.error);
            }
        })
        .catch(() => {
            alert("❌ Error sending OTP.");
        });
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

resendOtpLink.addEventListener("click", function (e) {
    resendOtpLink.classList.add("hidden");
    document.querySelectorAll(".otp-input").forEach(input => input.value = "");
    document.querySelector(".otp-input")?.focus();
    openOtpModal();
});

// Tự động chuyển sang ô tiếp theo khi nhập
document.querySelectorAll('.otp-input').forEach((input, idx, inputs) => {
    input.addEventListener('input', () => {
        if (input.value.length === 1 && idx < inputs.length - 1) {
            inputs[idx + 1].focus();
        }
    });
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Backspace' && !input.value && idx > 0) {
            inputs[idx - 1].focus();
        }
    });
});

function submitOtp() {
    const otpInputs = document.querySelectorAll(".otp-input");
    const formData = new URLSearchParams();
    const otpError = document.getElementById("otpError");
    const verifyBtn = document.getElementById("verifyOtpBtn");

    // Reset lỗi và trạng thái nút
    otpError.textContent = "";
    otpError.classList.add("hidden");
    verifyBtn.disabled = true;
    verifyBtn.innerHTML = `<svg class="inline w-4 h-4 mr-2 animate-spin text-white" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor"
              d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"></path>
      </svg>Verifying...`;

    otpInputs.forEach((input, i) => {
        formData.append(`d${i + 1}`, input.value.trim());
    });

    fetch(`${contextPath}/verify-otp`, {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: formData.toString()
    })
        .then(res => res.json())
        .then(data => {
            if (data.success === true) {
                window.location.href = `${contextPath}/change-password`;
            } else {
                otpError.textContent = data.message || "Invalid OTP. Please try again.";
                otpError.classList.remove("hidden");
                document.querySelectorAll(".otp-input").forEach(input => input.value = "");
                document.querySelector(".otp-input")?.focus();
            }
        })
        .catch(err => {
            console.error("OTP verification error:", err);
            otpError.textContent = "An error occurred. Please try again.";
            otpError.classList.remove("hidden");
        })
        .finally(() => {
            verifyBtn.disabled = false;
            verifyBtn.textContent = "Confirm";
        });
}