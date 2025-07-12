// Get email input to update email status button
const emailInput = document.getElementById("email");
const initialEmail = emailInput.dataset.original;
const contextPath = document.body.getAttribute("data-context-path");

emailInput.addEventListener("input", () => {
    const current = emailInput.value.trim();
    const existingStatus = document.getElementById("email-status") || document.getElementById("verify-email");

    if (current !== initialEmail) {
        if (!document.getElementById("verify-email")) {
            // language=HTML
            existingStatus.outerHTML = `
                <a href="#" id="verify-email"
                   class="bg-red-600 text-white text-sm rounded w-[140px] h-[38px] flex items-center justify-center hover:border-2">
                    Please verify</a>`;
        }
    } else {
        if (!document.getElementById("email-status")) {
            existingStatus.outerHTML = `
        <span id="email-status"
              class="bg-green-600 text-white text-sm rounded w-[140px] h-[38px] flex items-center justify-center">
          Verified
        </span>`;
        }
    }
});

// ========== LOAD DISTRICTS/WARDS ==========
// Fetch districts when a province is selected
function onProvinceChange(provinceSelect) {
    const provinceCode = provinceSelect.value;
    fetch(`${provinceSelect.dataset.url}?provinceCode=${provinceCode}`)
        .then(res => res.json())
        .then(data => {
            const districtSelect = document.getElementById("district");
            const wardSelect = document.getElementById("ward");
            districtSelect.innerHTML = '<option selected disabled>Choose District</option>';
            wardSelect.innerHTML = '<option selected disabled>Choose Ward</option>';
            data.forEach(d => {
                const opt = document.createElement("option");
                opt.value = d.districtCode;
                opt.textContent = d.name;
                districtSelect.appendChild(opt);
            });
        });
}

// Fetch wards when a district is selected
function onDistrictChange(districtSelect) {
    const districtCode = districtSelect.value;
    fetch(`${districtSelect.dataset.url}?districtCode=${districtCode}`)
        .then(res => res.json())
        .then(data => {
            const wardSelect = document.getElementById("ward");
            wardSelect.innerHTML = '<option selected disabled>Choose Ward</option>';
            data.forEach(w => {
                const opt = document.createElement("option");
                opt.value = w.wardCode;
                opt.textContent = w.name;
                wardSelect.appendChild(opt);
            });
        });
}

//------------
// Open and close modal
function openAvatarModal() {
    // 🔁 Xóa ảnh preview cũ
    const preview = document.getElementById("avatarPreview");
    preview.src = "";                       // reset src
    preview.classList.add("hidden");        // ẩn ảnh lại

    // 🔁 Reset file input nếu muốn chọn lại cùng ảnh
    document.getElementById("avatarInput").value = "";

    document.getElementById("avatarModal").classList.remove("hidden");
}

function closeAvatarModal() {
    document.getElementById("avatarModal").classList.add("hidden");
}

// Preview selected image
function previewAvatar(event) {
    const file = event.target.files[0];
    const preview = document.getElementById("avatarPreview");
    if (file) {
        preview.src = URL.createObjectURL(file);
        preview.classList.remove("hidden");
    }
}

// Upload avatar to CloudinaryServlet
function uploadAvatar() {
    const fileInput = document.getElementById("avatarInput");
    const file = fileInput.files[0];
    if (!file) {
        alert("Please choose an image.");
        return;
    }

    const formData = new FormData();
    formData.append("avatar", file);

    fetch(`${contextPath}/change-avatar`, {
        method: "POST", body: formData
    })
        .then(res => res.text())
        .then(uploadedUrl => {
            sessionStorage.setItem("hasPendingAvatar", "true");

            // ✅ 1. Show new avatar
            console.log("✅ Avatar uploaded successfully:", uploadedUrl);
            document.getElementById("currentAvatar").src = uploadedUrl;

            // ✅ 2. Save to hidden input
            console.log("🔒 Saving uploaded URL to hidden input");
            document.getElementById("avatarUrl").value = uploadedUrl;

            const actionsDiv = document.getElementById("avatarActions");
            actionsDiv.innerHTML = `
                <div class="flex gap-4">
                    <a href="#" id="changeAvatarLink" onclick="openAvatarModal(); return false;" class="text-blue-500 hover:underline mb-6">Change</a>
                    <a href="#" id="deleteAvatarLink" onclick="deleteAvatar(); return false;" class="text-red-500 hover:underline mb-6">Delete</a>
                </div>
                `;

            // ✅ 3. Close modal if needed
            console.log("🧩 Closing avatar modal");
            closeAvatarModal();
        })

        .catch(err => {
            console.error("Upload failed", err);
            alert("Upload failed. Please try again.");
        });
}

//--------------------
// event.preventDefault() trong function của click, nhưng event mà bạn dùng là từ DOMContentLoaded, không phải từ sự kiện click, dẫn tới preventDefault() không có tác dụng!
document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form");

    form.addEventListener("submit", function (e) {
        const fieldsToCheck = ["fullName", "email", "phoneNumber", "province", "district", "ward", "addressDetail", "avatarUrl"];
        let changed = false;

        for (const id of fieldsToCheck) {
            const el = document.getElementById(id);
            if (!el) continue;
            const original = el.getAttribute("data-original")?.trim();
            const current = el.value?.trim();
            if (original !== current) {
                changed = true;
                break;
            }
        }

        const genderContainer = document.getElementById("genderContainer");
        const originalGender = genderContainer?.getAttribute("data-original");
        const genderRadios = document.querySelectorAll("input[name='gender']");
        for (const radio of genderRadios) {
            if (radio.checked && radio.value !== originalGender) {
                changed = true;
                break;
            }
        }

        if (!changed) {
            e.preventDefault();
            alert("No changes detected.");
            // showMessageModal("No changes detected.");
            return;
        }

        const emailInput = document.getElementById("email");
        const initialEmail = emailInput.dataset.original?.trim();
        const currentEmail = emailInput.value?.trim();
        const verifiedEmail = emailInput.dataset.verifiedEmail;

        if (currentEmail !== initialEmail && currentEmail !== verifiedEmail) {
            e.preventDefault();
            alert("Please verify your new email before saving.");
            // showMessageModal("Please verify your new email before saving.");
            return;
        }

        if (!confirm("Do you want to save the changes?")) {
            e.preventDefault(); // User cancelled
        } else {
            form.action = contextPath + "/update-profile";
            form.method = "post";
            form.submit();
            sessionStorage.removeItem("hasPendingAvatar");
        }
    });
});

// ----------------------
document.addEventListener("click", function (e) {
    if (e.target && e.target.id === "verify-email") {
        e.preventDefault();

        const email = document.getElementById("email").value;

        fetch(`${contextPath}/check-email`, {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: `email=${encodeURIComponent(email)}`
        })
            .then(res => res.text())
            .then(msg => {
                if (msg === "Not exist") {
                    openOtpModal();
                } else if (msg === "Exist") {
                    errorDiv.textContent = "Email already exists";
                    errorDiv.classList.remove("hidden");
                } else {
                    // 🔴 Trường hợp khác (có thể là lỗi server)
                    alert("Unexpected error: " + msg);
                }
            });
    }
});

const otpModal = document.getElementById("otpModal");
const otpTimer = document.getElementById("otpTimer");
const otpCountdown = document.getElementById("otpCountdown");
const resendOtpLink = document.getElementById("resendOtpLink");
let otpInterval;

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
    verifyBtn.innerHTML = `<svg class="inline w-4 h-4 mr-2 animate-spin text-white" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor"
              d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"></path>
      </svg>Sending OTP...`;

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
    const email = document.getElementById("email").value;

    return fetch(`${contextPath}/send-otp`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `otpPurpose=verifyNewEmail&email=${encodeURIComponent(email)}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                console.log("OTP sent successfully.");
            } else {
                alert("Failed to send OTP.");
            }
        })
        .catch(() => {
            alert("Error sending OTP.");
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
    openOtpModal();
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
    console.log("Sending to server:", formData.toString());

    fetch(`${contextPath}/verify-otp`, {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: formData.toString()
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                // ✅ Update attribute dynamically in JS
                const emailInput = document.getElementById("email");
                emailInput.setAttribute("data-verified-email", emailInput.value.trim());

                // ✅ Cập nhật giao diện
                document.getElementById("verify-email")?.remove();
                const verifiedSpan = document.createElement("span");
                verifiedSpan.id = "email-status";
                verifiedSpan.className = "bg-green-600 text-white text-sm rounded w-[140px] h-[38px] flex items-center justify-center";
                verifiedSpan.innerText = "Verified";
                emailInput.parentNode.appendChild(verifiedSpan);

                closeOtpModal();
            } else {
                otpError.textContent = data.message || "Invalid OTP. Please try again.";
                document.querySelectorAll(".otp-input").forEach(input => input.value = "");
                document.querySelector(".otp-input")?.focus();
                otpError.classList.remove("hidden");
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

//-----------
function deleteAvatar() {
    // Xác nhận trước khi xoá
    if (!confirm("Are you sure you want to delete your avatar?")) return;

    const avatarImg = document.querySelector("#currentAvatar");
    const currentUrl = avatarImg?.src;

    // Gửi request POST để đánh dấu xoá
    fetch(`${contextPath}/mark-delete-avatar`, {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: `avatarUrl=${encodeURIComponent(currentUrl)}`
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to mark avatar for deletion.");
            }
            return response.text();
        })
        .then(returnedUrl => {
            const isNull = returnedUrl.trim() === "";
            console.log("returnedUrl: " + returnedUrl);

            // ✅ 1. Show new avatar (nếu null thì dùng ảnh mặc định để hiển thị)
            document.getElementById("currentAvatar").src = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png";

            // ✅ 2. Save to hidden input (null → giá trị rỗng)
            console.log("🔒 Saving uploaded URL to hidden input");
            document.getElementById("avatarUrl").value = "";

            const actionsDiv = document.getElementById("avatarActions");
            actionsDiv.innerHTML = `
                <div class="flex gap-4">
                    <a href="#" id="add-avatar-link" onclick="openAvatarModal(); return false;" class="text-blue-500 hover:underline mb-6">Add avatar</a>
                </div>
                `;
        })
        .catch(error => {
            console.error(error);
            alert("Error deleting avatar. Please try again.");
        });
}

//-------------------
cancelBtn.addEventListener("click", function (e) {
    e.preventDefault();

    const fieldsToCheck = ["fullName", "email", "phoneNumber", "province", "district", "ward", "addressDetail", "avatarUrl"];
    let changed = false;
    const changedFields = [];

    for (const id of fieldsToCheck) {
        const el = document.getElementById(id);
        if (!el) continue;
        const original = el.getAttribute("data-original")?.trim();
        const current = el.value?.trim();
        if (original !== current) {
            changed = true;
            changedFields.push({
                field: id,
                from: original,
                to: current
            });
        }
    }

    // Gender check
    const genderContainer = document.getElementById("genderContainer");
    const originalGender = genderContainer?.getAttribute("data-original");
    const genderRadios = document.querySelectorAll("input[name='gender']");
    for (const radio of genderRadios) {
        if (radio.checked && radio.value !== originalGender) {
            changed = true;
            changedFields.push({
                field: "gender",
                from: originalGender,
                to: radio.value
            });
            break;
        }
    }

    if (changed) {
        console.log("🔄 Changed fields:");
        changedFields.forEach(f => {
            console.log(`- ${f.field}: "${f.from}" ➝ "${f.to}"`);
        });

        const confirmCancel = confirm("You have made changes to the form. Do you want to discard them?");
        if (!confirmCancel) return;
    }

    window.location.href = `${contextPath}/home`;
});

//-------------
window.addEventListener("beforeunload", function (e) {
    const hasPendingAvatar = sessionStorage.getItem("hasPendingAvatar") === "true";
    if (hasPendingAvatar) {
        // Gửi request đến servlet để xóa ảnh
        navigator.sendBeacon(`${contextPath}/clear-temp-avatar`);
        sessionStorage.removeItem("hasPendingAvatar");
    }
});