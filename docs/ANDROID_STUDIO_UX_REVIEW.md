# Kiểm tra UX/UI trước khi merge

Nhánh: `feature/midterm-local-ux`, tách từ `develop` (`743cb885`).
PR phải giữ dạng Draft đến khi chủ repo chạy thử và đồng ý merge.

## Mở trên Android Studio

Nếu bạn đã có repo và các thay đổi riêng, commit hoặc stash chúng trước khi chuyển nhánh.

```bash
git fetch origin
git switch --track origin/feature/midterm-local-ux
```

Nếu nhánh đã có trên máy:

```bash
git switch feature/midterm-local-ux
git pull --ff-only origin feature/midterm-local-ux
```

Mở thư mục chứa `settings.gradle.kts`, chọn Sync Project with Gradle Files,
chọn emulator/điện thoại và Run `app`. Giữ các phiên bản Gradle/AGP/SDK đang
được khai báo trong repo; cài các thành phần Android Studio yêu cầu khi Sync.
`gradle-wrapper.jar` đã được bổ sung vì repo gốc bỏ qua toàn bộ `*.jar`.
JAR wrapper 9.4.0 chính thức có thể khởi động distribution được cấu hình sẵn;
không thay đổi Gradle distribution 9.8.2 hoặc checksum của dự án.

## Giới hạn của bản review

Đây là bản UX/UI local dùng dữ liệu minh họa, không cần Node.js/PostgreSQL.
Đăng nhập/đăng ký chỉ kiểm tra biểu mẫu và điều hướng; chưa xác thực hoặc tạo
user trên server. Không lưu mật khẩu. Hồ sơ thử nghiệm là một hồ sơ dùng chung
trên thiết bị, chưa gắn user đăng nhập. Đăng xuất không xóa hồ sơ local này.
Quên mật khẩu/OTP vẫn là các màn hình mẫu cũ, chưa gửi email hoặc đổi mật khẩu thật.
Ranking dùng ID của bộ dữ liệu local, không dùng ID backend. Dữ liệu, mô tả chi tiết,
đăng ký học bổng, theo dõi học bổng và các màn hình ngoài phạm vi vẫn là minh họa.
Chưa có tác vụ mạng nên chưa có vòng đời loading/lỗi mạng/thử lại; bổ sung khi nối API.

## Các trường hợp cần kiểm tra

1. **Đăng nhập/đăng ký:** gửi rỗng, email sai, mật khẩu dưới 8 ký tự;
   đăng ký thiếu tên/ngày sinh/số điện thoại. Không chuyển màn hình khi dữ liệu sai.
   Email đúng và mật khẩu từ 8 ký tự cho phép đi tiếp trong bản demo.
2. **Hồ sơ:** Profile → Account settings → Thông tin cá nhân (hoặc chạm tên/avatar).
   Nhấn Chỉnh sửa; sửa tên, trường, ngành, giới thiệu, kỹ năng và chọn ảnh.
   Lưu rồi quay về Profile: tên/ảnh cập nhật; đóng/mở app: hồ sơ còn lưu.
3. **Hủy:** sửa hồ sơ rồi Hủy hoặc Back; xác nhận bỏ thay đổi phải trả về thông tin
   đã lưu. Tiếp tục sửa phải giữ bản nháp. Không cho lưu tên chỉ có khoảng trắng.
   Hủy trình chọn ảnh không đổi ảnh. Xoay máy khi sửa phải giữ bản nháp.
4. **Tìm học bổng:** phối hợp từ khóa/quốc gia/bậc học/ngành/tài trợ;
   không có kết quả phải có hướng dẫn, Xóa tất cả bộ lọc trả về 5 học bổng.
5. **Deadline:** chọn ngày bắt đầu và ngày kết thúc; không thể chọn kết thúc trước
   bắt đầu. Hai đầu mút được tính trong kết quả. Chọn cùng ngày `30/11/2026`
   phải có KGSP. Hủy bước chọn ngày kết thúc không thay bộ lọc cũ.
   Xóa hạn nộp và xoay máy để kiểm tra nhãn/bộ lọc.
6. **Ranking:** Find Mentor ở card hoặc nút Tìm mentor trong chi tiết phải mở đúng
   học bổng. Global Excellence có Loan/Minh An; KGSP có Ha Linh; MEXT có Hieu;
   CSC có Mai; Taiwan ICDF hiển thị chưa có mentor. Chọn học bổng phải đổi danh sách.
7. **Tìm mentor:** nhập tên không có → trạng thái rỗng; Reset mentor khôi phục
   mentor của học bổng hiện tại. Reset học bổng yêu cầu chọn lại, không hiện ranking
   của học bổng cũ. Xoay máy phải giữ học bổng đang chọn.
8. Kiểm tra màn hình nhỏ, bàn phím mở và chữ lớn; các nút Lưu/Hủy phải cuộn tới được.

## Kiểm tra đã thực hiện trong môi trường chỉnh sửa

- Parse cú pháp toàn bộ Java và XML; đối chiếu resource references; `git diff --check`.
- Kiểm tra riêng mô hình/catalog Java và tính nhất quán điểm ranking.
- Wrapper JAR đối chiếu SHA-256 chính thức:
  `55243ef57851f12b070ad14f7f5bb8302daceeebc5bce5ece5fa6edb23e1145c`.
- Chưa xác nhận build APK hoặc chạy emulator: Gradle dừng ở bước tải distribution
  do môi trường không kết nối được; không có Android SDK/emulator để chạy UI.
  Cần Gradle Sync/Run và kiểm tra các tình huống trên máy của bạn trước merge.
