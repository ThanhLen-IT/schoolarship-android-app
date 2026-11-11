# Scholarship Android App

Code Android đang được phát triển trên nhánh [`develop`](https://github.com/ThanhLen-IT/schoolarship-android-app/tree/develop).

Nhánh `main` hiện chỉ giữ tài liệu giới thiệu và giấy phép. Không phát triển tính năng trên `main`.

## Bắt đầu làm việc

```bash
git clone --branch develop https://github.com/ThanhLen-IT/schoolarship-android-app.git
cd schoolarship-android-app
git switch -c feature/ten-chuc-nang
```

Với bản clone đã có, hãy commit hoặc stash công việc đang làm trước khi chuyển nhánh:

```bash
git fetch origin
git switch develop
git pull --ff-only origin develop
```

Mở dự án trên `develop` bằng Android Studio. Các pull request phát triển tính năng phải chọn base branch là `develop`.

## Lưu ý về việc dọn main

Commit dọn `main` chỉ phục vụ cách tổ chức nhánh. Không merge/cherry-pick commit xóa code trên `main` vào `develop`.

Bản `main` trước khi dọn được lưu tại nhánh `backup/main-before-develop-only-20261111`. Lịch sử commit được giữ nguyên.
