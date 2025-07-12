const contextPath = document.body.getAttribute("data-context-path");

//-------------
// 👁 Show the Forgot Password modal
function openForgotModal() {
    document.getElementById("forgotModal").classList.remove("hidden");
}

// ❌ Hide the modal
function closeForgotModal() {
    document.getElementById("forgotModal").classList.add("hidden");
}

// 📬 Submit email to check existence and send OTP
function submitForgotEmail() {
    const emailInput = document.getElementById("forgotEmail");
    const email = emailInput.value.trim();
    const errorDiv = document.getElementById("forgotError");

    // 🧹 Hide previous error if any
    errorDiv.classList.add("hidden");
    errorDiv.textContent = "";

    // 🚫 Validate: Email must not be empty
    if (!email) {
        errorDiv.textContent = "Email is required.";
        errorDiv.classList.remove("hidden");
        return;
    }

    // 📡 Send request to server to check if email exists
    fetch(`${contextPath}/check-email`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `email=${encodeURIComponent(email)}`
    })
        .then(res => res.text())
        .then(msg => {
            if (msg === "Exist") {
                openOtpModal(email); // ✅ Email tồn tại → mở modal OTP
            } else if (msg === "Not exist") {
                // ❌ Email không tồn tại → báo lỗi
                errorDiv.textContent = "This email does not exist.";
                errorDiv.classList.remove("hidden");
            } else {
                // 🔴 Trường hợp khác (có thể là lỗi server)
                alert("Unexpected error: " + msg);
            }
        })
        .catch(() => {
            // ❌ Unexpected error
            errorDiv.textContent = "Something went wrong. Please try again.";
            errorDiv.classList.remove("hidden");
        });
}

const otpModal = document.getElementById("otpModal");
const otpTimer = document.getElementById("otpTimer");
const otpCountdown = document.getElementById("otpCountdown");
const resendOtpLink = document.getElementById("resendOtpLink");
let otpInterval;
let currentEmailInOtpModal = "";

function openOtpModal(email) {
    currentEmailInOtpModal = email;
    clearInterval(otpInterval);

    const forgotModal = document.getElementById("forgotModal");
    if (forgotModal) forgotModal.classList.add("hidden");

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
    verifyBtn.innerHTML = `<svg class="inline w-4 h-4 mr-2 animate-spin text-white" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor"
              d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"></path>
      </svg>Sending OTP...`;

    sendOtp(currentEmailInOtpModal).then(() => {
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

function sendOtp(currentEmailInOtpModal) {
    return fetch(`${contextPath}/send-otp`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `otpPurpose=resetPassword&email=${encodeURIComponent(currentEmailInOtpModal)}`
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
    openOtpModal(currentEmailInOtpModal);
});

// Auto-focus and move to next box
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
    const otpError = document.getElementById("otpError");
    const verifyBtn = document.getElementById("verifyOtpBtn");

    // 🧹 Reset UI before submission
    otpError.textContent = "";
    otpError.classList.add("hidden");
    verifyBtn.disabled = true;
    verifyBtn.innerHTML = `<svg class="inline w-4 h-4 mr-2 animate-spin text-white" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor"
              d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"></path>
      </svg>Verifying...`;

    // 📦 Collect 6-digit OTP
    const formData = new URLSearchParams();
    let valid = true;

    otpInputs.forEach((input, idx) => {
        const value = input.value.trim();
        if (!value) valid = false;
        formData.append(`d${idx + 1}`, value);
    });

    if (!valid) {
        otpError.textContent = "Please enter all 6 digits.";
        otpError.classList.remove("hidden");
        verifyBtn.disabled = false;
        verifyBtn.textContent = "Confirm";
        return;
    }

    // 📡 Submit OTP to backend for validation
    fetch(`${contextPath}/verify-otp`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: formData.toString()
    })
        .then(res => {
            if (res.redirected) {
                // ✅ Trường hợp redirect (VD: chuyển tới set-password)
                window.location.href = res.url;
                return null;
            }
            return res.json();
        })
        .then(data => {
            if (!data) return; // đã redirect
            if (data.success) {
                // ✅ Nếu trả về JSON success → redirect bằng code (hiếm)
                window.location.href = `${contextPath}/reset-password`;
            } else {
                // ❌ Sai OTP → hiện lỗi
                otpError.textContent = data.message || "Invalid OTP. Please try again.";
                otpError.classList.remove("hidden");
                document.querySelectorAll(".otp-input").forEach(input => input.value = "");
                document.querySelector(".otp-input")?.focus();
            }
        })
        .catch(err => {
            console.error("OTP verification error:", err);
            otpError.textContent = "Something went wrong. Please try again.";
            otpError.classList.remove("hidden");
        })
        .finally(() => {
            verifyBtn.disabled = false;
            verifyBtn.textContent = "Confirm";
        });
}
