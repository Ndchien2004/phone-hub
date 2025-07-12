# Hướng dẫn Kiểm thử (Testing Guide)

Tài liệu này cung cấp các thông tin cần thiết để kiểm thử các luồng chức năng trong dự án, đặc biệt là các cổng thanh toán.

## 1. Thông tin Thẻ Test VNPAY

Sử dụng các thông tin thẻ dưới đây trên trang thanh toán Sandbox của VNPAY.

### 1.1. Thẻ nội địa (NAPAS)

| STT | Ngân hàng | Số thẻ                | Tên chủ thẻ   | Ngày phát hành | Mật khẩu OTP | Ghi chú             |
|:----|:-----------|:----------------------|:--------------|:---------------|:-------------|:--------------------|
| 1   | NCB        | `9704198526191432198` | NGUYEN VAN A  | 07/15          | `123456`     | Giao dịch thành công|
| 2   | NCB        | `9704195798459170488` | NGUYEN VAN A  | 07/15          |              | Không đủ số dư      |
| 3   | NCB        | `9704192181368742`    | NGUYEN VAN A  | 07/15          |              | Thẻ chưa kích hoạt  |
| 4   | NCB        | `9704193370791314`    | NGUYEN VAN A  | 07/15          |              | Thẻ bị khóa         |
| 5   | NCB        | `9704194841945513`    | NGUYEN VAN A  | 07/15          |              | Thẻ bị hết hạn      |
| 12  | EXIMBANK   | `9704310005819191`    | NGUYEN VAN A  | 10/26          |              | Giao dịch thành công|

### 1.2. Thẻ quốc tế

**Thông tin chung:**
- **Tên chủ thẻ:** `NGUYEN VAN A`
- **CVC/CVV:** `123`
- **Email:** `test@gmail.com`
- **Địa chỉ:** `22 Lang Ha`
- **Thành phố:** `Ha Noi`

| STT | Loại thẻ             | Số thẻ               | Ngày hết hạn | Ghi chú             |
|:----|:---------------------|:---------------------|:-------------|:--------------------|
| 6   | VISA (No 3DS)        | `4456530000001005`   | 12/26        | Giao dịch thành công|
| 7   | VISA (3DS)           | `4456530000001096`   | 12/26        | Giao dịch thành công|
| 8   | MasterCard (No 3DS)  | `5200000000001005`   | 12/26        | Giao dịch thành công|
| 9   | MasterCard (3DS)     | `5200000000001096`   | 12/26        | Giao dịch thành công|
| 10  | JCB (No 3DS)         | `3337000000000008`   | 12/26        | Giao dịch thành công|
| 11  | JCB (3DS)            | `3337000000200004`   | 12/24        | Giao dịch thành công|

## 2. Thông tin Test các cổng thanh toán khác

*(Bổ sung thông tin test cho MoMo, PayOS... vào đây sau này)*