# System Design & Architecture Audit — GLOBAL SUCCESS Android App

Báo cáo phân tích hệ thống giao diện, Design Tokens, Component tái sử dụng, Screen Mapping và Interaction Mapping trích xuất trực tiếp từ mã nguồn ứng dụng Android Native **GLOBAL SUCCESS**.

---

## 2.1. Design Tokens

Bảng tổng hợp tất cả các giá trị token thiết kế được định nghĩa trong tài nguyên dự án (`colors.xml`, `colors_semantic.xml`, `dimens.xml`, `typography.xml`, `styles.xml`, `@font/*`):

| Thành phần Token | Thuộc tính | Giá trị Mã nguồn / Đơn vị | Mô tả & Ứng dụng |
| :--- | :--- | :--- | :--- |
| **Color Primary** | Brand Navy | `#0F0954` (`@color/color_primary`) | Màu chủ đạo cho Header, Bottom Nav, Nút bấm chính, Tiêu đề thương hiệu |
| **Color Surface** | Surface / Background | `#FFFFFF` (`@color/color_surface`) | Nền màn hình chính, Nền Card, Nền Toolbar |
| **Color Background** | Secondary Background | `#E0ECFF` / `#F8FAFC` (`@color/color_background`) | Nền Banner Slider, Nền Chip phụ, Nền tin nhắn đi |
| **Color Text Primary** | Main Text | `#0F172A` (`@color/color_text_primary`) | Văn bản tiêu đề, tên Mentor, nội dung chính |
| **Color Text Hint** | Muted Text | `#64748B` (`@color/color_text_hint`) | Văn bản phụ, thời gian (timestamp), placeholder |
| **Color Border** | Input Border | `#E2E8F0` / `#ABABAB` (`@color/color_border`) | Viền ô nhập liệu, viền phân cách card, divider |
| **Status Approved** | Green Badge | BG: `#DCFCE7` \| Text: `#15803D` | Trạng thái Đã xác thực / Đã duyệt / Đã nhận |
| **Status Pending** | Amber Badge | BG: `#FEF3C7` \| Text: `#B45309` | Trạng thái Đang chờ duyệt / Đang chờ phản hồi |
| **Status Rejected** | Red Badge | BG: `#FEE2E2` \| Text: `#B91C1C` | Trạng thái Chưa phù hợp / Bị từ chối |
| **Font Family** | Brand Heading | `Suez One` (`@font/suez_one`) | Logo thương hiệu GLOBAL SUCCESS, Tiêu đề Banner |
| **Font Family** | UI Body Text | `Inter` (`@font/inter_italic` / sans-serif) | Nội dung bảng, tin nhắn, thông tin hồ sơ |
| **Font Family** | Accent CTA | `Tilt Warp` (`@font/tilt_warp`) | Nhãn nút bấm nổi bật |
| **Font Size** | Hero Heading | `18sp` | Tiêu đề lớn màn hình (Banner, Become a Mentor) |
| **Font Size** | Screen Title | `15sp` / `16sp` | Tiêu đề Toolbar, Tiêu đề Section |
| **Font Size** | Body Primary | `13sp` / `14sp` | Nội dung chính, tên người dùng, tên học bổng |
| **Font Size** | Muted Label | `11sp` / `12sp` | Môt tả phụ, ngày tháng, hạn nộp |
| **Font Size** | Badge & Tag | `10sp` / `11sp` | Nhãn trạng thái, chip phân loại |
| **Corner Radius** | Card & Input | `12dp` / `16dp` | Ô nhập liệu (`bg_input_field`), Card học bổng |
| **Corner Radius** | Pill & Chip | `999dp` / `16dp` | Nút bấm chính (`bg_welcome_button_primary`), Nút Reset |
| **Corner Radius** | Avatar Image | `50%` (Circular) | Ảnh đại diện người dùng, Mentor avatar |
| **Spacing** | Screen Margin | `16dp` | Khoảng cách lề trái/phải màn hình |
| **Spacing** | Card Internal Padding| `12dp` / `16dp` | Khoảng cách lót trong Card |
| **Spacing** | Item Gap | `8dp` / `12dp` | Khoảng cách giữa các ô danh sách |

---

## 2.2. Component System (Thành phần tái sử dụng)

Phân loại các thành phần UI xuất hiện nhiều lần trong ứng dụng và đã được đóng gói/dùng chung:

### 1. Button (Nút bấm)
- **Primary CTA Button** (`bg_welcome_button_primary.xml`): Nút màu xanh chàm mỏ neo (`#0F0954`), chữ trắng, bo góc oval (`999dp`), chiều cao 42dp–48dp.
- **Secondary Outlined Button** (`bg_welcome_button_secondary.xml`): Nút viền bo tròn màu xanh nhạt (`#E0ECFF`), chữ màu xanh chàm (`#0F0954`).
- **Reset & Filter Action Chip**: Nút nhỏ gọn bo viền chữ `"Reset"` hoặc nhãn chọn bộ lọc.

### 2. TextField & Search Bar (Ô nhập liệu & Thanh tìm kiếm)
- **Input Field** (`bg_input_field.xml`): Nền trắng, viền `#E2E8F0`, bo góc 12dp, chiều cao 40dp–48dp.
- **Search Bar**: Ô nhập liệu kết hợp icon kính lúp (`ic_search`) bên trái, có nút xóa hoặc lọc đi kèm.

### 3. Navigation Item (Thanh điều hướng)
- **Shared Bottom Navigation Bar** (`layout_bottom_nav`): Thanh cố định ở đáy màn hình chiều cao 60dp, nền xanh chàm (`#0F0954`), chứa 5 tab chính (`Home`, `Ranking`, `Community`, `Messages`, `Profile`).

### 4. Card (Thẻ hiển thị thông tin)
- **Scholarship Result Card** (`item_scholarship_result_card.xml`): Thẻ thông tin học bổng gồm logo, tên, bậc học, hạn nộp, nút follow.
- **Mentor Ranking Row** (`item_mentor_ranking_row.xml`): Hàng hiển thị Mentor trong bảng xếp hạng 7 cột.
- **Application Card** (`item_my_application.xml`): Thẻ đơn ứng tuyển với trạng thái (Đã nhận, Đang chờ, Chưa phù hợp) và nút nhắn tin.
- **Notification Card** (`item_notification_card.xml`): Thẻ thông báo kèm icon phân loại và chấm xanh chưa đọc.
- **Saved Items Card** (`item_saved_scholarship_card.xml` / `item_bookmarked_post_card.xml`): Thẻ bài viết/học bổng đã lưu kèm nút `Bỏ lưu` / `Bs Follow`.
- **Chat Bubbles** (`item_chat_incoming.xml`, `item_chat_outgoing.xml`, `item_chat_attachment.xml`): Bong bóng tin nhắn văn bản và file đính kèm.

### 5. Modal & Dropdown (Hộp thoại & Bộ lọc)
- **Filter Dialogs** (`AlertDialog`): Hộp thoại chọn Quốc gia (Đông Nam Á, Nhật Bản...), Bậc học, Ngành học, Tài trợ, Sắp xếp.
- **Action Confirmation Dialogs**: Hộp thoại Đăng xuất (`logout_dialog`) và Hộp thoại Xóa tài khoản (`delete_account_dialog`).

### 6. Table (Bảng số liệu)
- **Mentor Ranking Table**: Bảng xếp hạng Mentor 7 cột (`Rank`, `Mentor Name`, `Total`, `Mentee Success Rate`, `Average Rating`, `Completion Rate`, `Response Rate`) hỗ trợ cuộn ngang `HorizontalScrollView`.

### 7. Header (Thanh tiêu đề màn hình)
- **Top App Bar**: Thanh điều hướng trên cùng kèm nút Back (`btn_back_*`), tiêu đề màn hình, và các hành động phụ (Notification, Share, Mark all as read).

---

## 2.3. Screen Mapping (Bản đồ màn hình)

Danh sách toàn bộ 18+ màn hình trong dự án và mối quan hệ điều hướng giữa chúng:

### Bảng chi tiết các màn hình:

| Màn hình (Activity) | Chức năng chính | Màn hình liên kết đến |
| :--- | :--- | :--- |
| `MainActivity` | Màn hình Chào mừng (Onboarding) | `SignInActivity`, `SignUpActivity` |
| `SignInActivity` | Đăng nhập tài khoản | `HomeActivity`, `SignUpActivity`, `ForgotPasswordActivity` |
| `SignUpActivity` | Đăng ký tài khoản mới | `RegistrationSuccessActivity`, `SignInActivity` |
| `RegistrationSuccessActivity` | Thông báo đăng ký thành công | `SignInActivity` |
| `ForgotPasswordActivity` | Nhập Email khôi phục mật khẩu | `VerifyEmailActivity` |
| `VerifyEmailActivity` | Nhập mã OTP xác minh Email | `PasswordResetConfirmationActivity` |
| `PasswordResetConfirmationActivity` | Xác nhận đặt lại mật khẩu | `SetNewPasswordActivity` |
| `SetNewPasswordActivity` | Đặt mật khẩu mới | `SignInActivity` |
| `HomeActivity` | Trang chủ Dashboard & Banner Carousel | `ScholarshipDiscoveryActivity`, `MentorRankingActivity`, `CommunityActivity`, `MessagesActivity`, `ProfileActivity`, `NotificationsActivity` |
| `ScholarshipDiscoveryActivity` | Khám phá Học bổng (Lọc & Sắp xếp) | `ScholarshipDetailActivity`, `HomeActivity` |
| `ScholarshipDetailActivity` | Chi tiết Học bổng & Nút nộp đơn | `ScholarshipDiscoveryActivity` |
| `MentorRankingActivity` | Bảng xếp hạng Mentor 7 cột | `HomeActivity`, `ProfileActivity` |
| `CommunityActivity` | Bảng tin Cộng đồng | `CreatePostActivity`, `HomeActivity` |
| `CreatePostActivity` | Tạo bài viết mới | `TextComposerActivity`, `CommunityActivity` |
| `TextComposerActivity` | Trình soạn thảo văn bản bài viết | `CreatePostActivity` |
| `MessagesActivity` | Danh sách đoạn Chat & Contact | `ChatDetailActivity`, `HomeActivity` |
| `ChatDetailActivity` | Chi tiết cuộc trò chuyện | `MessagesActivity` |
| `NotificationsActivity` | Danh sách thông báo | `HomeActivity`, `ProfileActivity` |
| `ProfileActivity` | Hồ sơ (Chế độ Mentee & Mentor) | `AccountSettingsActivity`, `HelpCenterActivity`, `BecomeMentorActivity`, `MyApplicationsActivity`, `SavedScholarshipsActivity` |
| `AccountSettingsActivity` | Cài đặt tài khoản (Đổi MK, Xóa TK) | `ForgotPasswordActivity`, `MainActivity` |
| `HelpCenterActivity` | Trung tâm hỗ trợ & Góp ý | `ProfileActivity` |
| `BecomeMentorActivity` | Tìm học bổng Đăng ký làm Mentor | `SubmitVerificationActivity`, `ProfileActivity` |
| `SubmitVerificationActivity` | Upload minh chứng PDF (≤ 5MB) | `VerificationStatusActivity` |
| `VerificationStatusActivity` | Trạng thái duyệt hồ sơ Mentor | `ProfileActivity` |
| `MyApplicationsActivity` | Quản lý Đơn ứng tuyển của tôi | `ProfileActivity`, `ChatDetailActivity` |
| `SavedScholarshipsActivity` | Mục đã lưu (Học bổng & Bài viết) | `ScholarshipDetailActivity`, `ProfileActivity` |

---

## 2.4. Interaction Mapping (Sự kiện & Kết quả thực tế)

Bảng phân biệt rõ ràng giữa **Giao diện tĩnh** và **Hành vi tương tác thực tế** trong mã nguồn:

| Thành phần UI | Sự kiện | Kết quả Tương tác Thực tế trong Mã nguồn | Loại hành vi |
| :--- | :--- | :--- | :--- |
| `btnLogin` (`activity_welcome`) | Click | Mở `SignInActivity` | Chuyển màn hình |
| `btn_login` (`activity_sign_in`) | Click | Validate Email/Password, mở `HomeActivity` | Logic & Navigation |
| `btn_register` (`activity_sign_up`) | Click | Validate Form, mở `RegistrationSuccessActivity` | Logic & Navigation |
| `et_scholarship_search` (`activity_scholarship_discovery`) | TextChanged | Lọc danh sách học bổng theo từ khóa thời gian thực | Real-time Filter |
| `layout_full_filter` (`Country`) | Click | Hiển thị `AlertDialog` chọn Quốc gia (Đông Nam Á, Nhật Bản...) | Modal Dialog |
| `tv_sort` (`Scholarship Discovery`) | Click | Hiển thị `AlertDialog` sắp xếp (Hạn gần nhất, mới nhất...) | Modal Dialog |
| `card_scholarship_*` | Click | Mở `ScholarshipDetailActivity` kèm data học bổng | Navigation + Extra Data |
| `btn_apply_now` (`Scholarship Detail`) | Click | Hiển thị Toast thông báo chuyển tới link đăng ký | Local Action / UI Feedback |
| `btn_download_form` | Click | Hiển thị Toast thông báo tải mẫu đơn PDF | Local Action / UI Feedback |
| `tv_scholarship_ranking_title` | TextChanged | Nhập tên học bổng, tự động đồng bộ với ô filter bên dưới | Bidirectional Sync |
| `btn_reset_scholarship` | Click | Xóa sạch ô nhập filter học bổng và đặt lại tiêu đề mặc định | Reset State |
| `chip_feedback_topic` (`Help Center`) | Click | Đổi màu chữ highlight chủ đề được chọn | Visual State Change |
| `btn_submit_feedback` | Click | Hiển thị Toast xác nhận đã gửi góp ý theo chủ đề chọn | Local Action |
| `et_help_search` (`Help Center`) | TextChanged | Lọc câu hỏi FAQ thời gian thực | Real-time Filter |
| `btn_verification_status` | Click | Lọc danh sách học bổng cần verify theo trạng thái | Filter State Change |
| `layout_upload_area` | Click | Mở Storage Access Framework chọn file `.pdf` ($\le$ 5MB) | System File Picker |
| `btn_submit_document` | Click | Validate file PDF, chuyển trạng thái `Pending Review` và mở `VerificationStatusActivity` | State Update & Nav |
| `btn_toggle_user_mode` (`Profile`) | Click | Chuyển đổi giữa Chế độ Mentee và Verified Mentor (ẩn/hiện badge và card xếp hạng) | UI State Toggle |
| `row_logout` (`Account Settings`) | Click | Mở `AlertDialog` xác nhận Đăng xuất, xóa task và về `SignInActivity` | Navigation + Clear Task |
| `row_delete_account` | Click | Mở `AlertDialog` xác nhận Xóa tài khoản, xóa task và về `MainActivity` | Navigation + Clear Task |
| `filter_all` / `filter_pending` | Click | Chuyển tab lọc danh sách Đơn ứng tuyển hoặc Bài viết đã bookmark | Tab State Switch |
| `btn_unbookmark` (`Saved Items`) | Click | Xóa bài viết khỏi mục đã lưu, cập nhật lại RecyclerView và empty state | Item Removal & UI Update |
| `btn_mark_all_read` (`Notifications`) | Click | Đổi toàn bộ trạng thái `isRead = true`, ẩn chấm xanh chưa đọc | Batch State Update |
| `btn_send` (`Chat Detail`) | Click | Thêm tin nhắn văn bản vào danh sách chat và cuộn mượt xuống cuối | Real-time List Insert |
| `nav_home`, `nav_community`... | Click | Chuyển đổi qua lại giữa 5 tab chính trên Bottom Navigation Bar | Root Navigation |
