# Rà soát câu đọc trong giao dịch

Ngày rà soát: 13/11/2026 · Phạm vi: `main` tại `394e951`, mọi giao dịch
(`setAutoCommit(false)`) trong `src/main/java/com/bluemoon/dao`.

## Lý do rà soát

Ngày 13/11/2026, `PhieuThuIT#nhieuLuongVuaSuaVuaThuCungMotKhoanKhongDeadlock`
trượt một lần trên CI (PR #42): có một dòng phiếu thu còn hiệu lực mà số tiền
khác số tiền phải nộp hiện tại. Nguyên nhân nằm ở `CongNoDaoImpl.suaSoTien`:

1. Câu đầu tiên của giao dịch, `hoCuaKhoan`, là `SELECT` thường. Ở mức
   `REPEATABLE READ` (mặc định của InnoDB), câu đọc thường (consistent read)
   đầu tiên của giao dịch **chụp snapshot**; mọi câu đọc thường sau đó trong
   giao dịch đều đọc snapshot này.
2. Giao dịch sau đó chờ khóa hộ, rồi khóa khoản (`FOR UPDATE`). Trong lúc
   chờ, máy khác lập phiếu thu khoản đó và commit.
3. Câu kiểm tra "đã thu" (`SQL_DA_THU`) cũng là `SELECT` thường, nên đọc
   snapshot cũ, không thấy phiếu vừa lập và cho sửa số tiền của khoản đã
   thu (AC-026).

Câu đọc có khóa (`FOR UPDATE`, `FOR SHARE`) và câu `UPDATE`, `DELETE`,
`INSERT … SELECT` thì luôn đọc bản mới nhất đã commit (current read), không
dùng snapshot.

**Quy tắc** (ghi vào [architecture.md](../design/architecture.md#giao-dịch-và-khóa)):
câu đọc dùng để ra quyết định dưới khóa phải là đọc có khóa, hoặc là câu đọc
thường **đầu tiên** của giao dịch và chạy sau khi đã giữ khóa mà mọi giao
dịch ghi vào dữ liệu đó cũng phải lấy.

## Kết quả

Ký hiệu: **Sửa**: đã đổi trong PR này; **Đúng**: không đổi, kèm lý do.

| Giao dịch | Câu đọc dùng để quyết định | Kết luận |
|---|---|---|
| `CongNoDaoImpl.suaSoTien` | `SQL_DA_THU`: khoản đã có phiếu thu còn hiệu lực chưa | **Sửa.** Đây là lỗi: `hoCuaKhoan` (đọc thường) chụp snapshot trước khi khóa. Đổi thành `SQL_DA_THU_KHOA` (`… FOR SHARE`), chạy sau khi đã khóa hộ rồi khoản; thứ tự khóa vẫn là hộ → phai_nop như lập phiếu. Kiểm bằng `SuaSoTienDocKhoaIT` |
| `CongNoDaoImpl.suaSoTien` | `SQL_KHOA_KHOAN`: số tiền cũ ghi vào nhật ký | Đúng: `FOR UPDATE` |
| `CongNoDaoImpl.suaSoTien` | `hoCuaKhoan`: hộ của khoản để khóa | Đúng: `ho_id` của một khoản phải nộp không bao giờ đổi, đọc snapshot cũng đúng |
| `CongNoDaoImpl.suaSoTien` | `SQL_MO_TA_KHOAN`: tên khoản, tháng, mã căn hộ cho nhật ký | Đúng: chỉ để ghi nội dung nhật ký, không quyết định gì. Mã căn hộ có thể là bản trước một lần sửa hộ đang diễn ra ở máy khác |
| `CongNoDaoImpl.chot` | `SQL_KHOA_DOT` (`FOR UPDATE`); `SQL_THEM_KHOAN` (`INSERT … SELECT … WHERE NOT EXISTS`) | Đúng: cả hai là current read; hai lần chốt cùng tháng chạy nối tiếp nhờ khóa dòng `dot_thu`, `UNIQUE (dot_thu_id, ho_id, khoan_phi_id)` chặn thêm trùng |
| `PhieuThuDaoImpl.lapPhieu` | `SQL_KHOA_PHAI_NOP`: khoản thuộc hộ, số tiền không đổi | Đúng: `FOR UPDATE` |
| `PhieuThuDaoImpl.lapPhieu` | `SQL_DA_THU`: khoản đã thu chưa; `SQL_CO_DONG_GOP`: quỹ đã ghi đóng góp chưa | Đúng, không đổi: trước chúng chỉ có câu khóa (`khoaHo`, `SQL_KHOA_PHAI_NOP`), nên câu đọc thường đầu tiên chụp snapshot **sau** khi đã giữ khóa hộ. Mọi giao dịch tạo dòng phiếu cho hộ này (lập phiếu) đều phải lấy khóa hộ, nên không phiếu nào commit sau snapshot mà liên quan. Hủy phiếu không khóa hộ, nhưng chỉ làm dòng hết hiệu lực: đọc bản cũ thì từ chối thu (an toàn, thu lại được). Không đổi sang `FOR SHARE`: câu này tìm theo `phai_nop_da_thu` trên chỉ mục `UNIQUE`; khi chưa có dòng, `FOR SHARE` khóa khoảng trống (gap lock), và hai máy thu cho hai hộ khác nhau có khóa khoảng trống chung rồi cùng `INSERT` vào khoảng đó sẽ deadlock. Thu trùng còn bị chặn ở CSDL bằng `uq_ptct_phai_nop_da_thu` |
| `PhieuThuDaoImpl.huy` | `SQL_KHOA_PHIEU`: phiếu còn, chưa hủy | Đúng: `FOR UPDATE` |
| `PhieuThuDaoImpl.lapPhieu`, `huy` | `HoGiaDinhDaoImpl.maCanHo` | Đúng: chỉ cho nội dung nhật ký |
| `DotCuTruDaoImpl.them` | `SQL_CHONG_NGAY`: đợt mới chồng ngày đợt cũ | Đúng, không đổi: câu đọc thường đầu tiên, sau `SQL_KHOA_NHAN_KHAU` (`FOR UPDATE`). Mọi giao dịch thêm đợt cho một nhân khẩu đã có đều khóa dòng nhân khẩu đó trước; thêm hộ hay nhân khẩu kèm đợt đầu chỉ ghi đợt của người mới, chưa ai khác thấy. Không đổi sang `FOR SHARE` vì quét khoảng trên `idx_ttv_nhan_khau` cũng khóa khoảng trống, hai người cạnh nhau trong chỉ mục ghi đợt cùng lúc có thể deadlock |
| `HoGiaDinhDaoImpl.capNhat` | `SQL_KHOA_ID`: giá trị cũ để ghi nhật ký | Đúng: `FOR UPDATE`. Trùng mã căn hộ đang ở do `uq_ho_ma_dang_o` chặn |
| `HoGiaDinhDaoImpl.them` | – | Đúng: chỉ `INSERT`; trùng mã căn hộ, CCCD, chủ hộ do ràng buộc `UNIQUE` chặn |
| `HoGiaDinhDaoImpl.danhDauChuyenDi` | `BienDongDaoImpl.ghiCaHoChuyenDi` (`INSERT … SELECT` nhân khẩu đang ở) | Đúng: chạy sau `UPDATE ho_gia_dinh` (khóa dòng hộ), `INSERT … SELECT` là current read |
| `NhanKhauDaoImpl.capNhat` | `SQL_KHOA_ID`: giá trị cũ | Đúng: `FOR UPDATE` |
| `NhanKhauDaoImpl.capNhatTrangThai` | `SQL_KHOA`: đang ở hay không (quyết định ghi biến động, nhật ký) | Đúng: `FOR UPDATE` |
| `NhanKhauDaoImpl.doiChuHo` | `SQL_KHOA_HAI`: chủ hộ cũ còn là chủ hộ đang ở, chủ hộ mới đang ở | Đúng: `FOR UPDATE` cả hai dòng trong một câu |
| `NhanKhauDaoImpl.them`, `themKemDot` | – | Đúng: chỉ `INSERT`, ràng buộc `UNIQUE` chặn trùng |
| `KhoanPhiDaoImpl.capNhatDonGia` | `SQL_KHOA_ID`: đơn giá cũ | Đúng: `FOR UPDATE` |
| `KhoanPhiDaoImpl.them` | – | Đúng: chỉ `INSERT`, `uq_kp_ten_dot` chặn trùng |
| `TaiKhoanDaoImpl.capNhatCoNhatKy` (vai trò, khóa/mở khóa, đặt lại mật khẩu) | `SQL_KHOA_ID`: giá trị cũ | Đúng: `FOR UPDATE` |
| `TaiKhoanDaoImpl.them` | – | Đúng: chỉ `INSERT`, tên đăng nhập `UNIQUE` |

Các kiểm tra ở Service trước giao dịch (vd `CongNoService.suaSoTien` gọi
`daCoPhieuThu`, `tonTaiCccd`, `tonTaiMaDangO`) chỉ để báo lỗi sớm cho người
dùng; quyết định cuối nằm trong giao dịch DAO hoặc ràng buộc CSDL ở bảng trên.

## Kiểm thử

- `SuaSoTienDocKhoaIT#phieuLapGiuaChungThiKhongSuaDuocKhoanDaThu`: máy B
  chạy `hoCuaKhoan` (chụp snapshot), điểm chèn `sauKhiDocHo` cho máy A lập
  phiếu trên kết nối khác và commit, rồi B khóa và kiểm tra. Mã cũ: `suaSoTien`
  trả `true`, số tiền đổi (test trượt). Mã mới: trả `false`, số tiền và
  nhật ký không đổi. Chạy tuần tự trên một luồng, không `sleep`, không phụ
  thuộc thứ tự luồng.
- `SuaSoTienDocKhoaIT#khongCoPhieuGiuaChungThiVanSuaDuoc`: đọc có khóa không
  chặn việc sửa khi không có phiếu.
- `PhieuThuIT#nhieuLuongVuaSuaVuaThuCungMotKhoanKhongDeadlock` vẫn đạt (không
  deadlock giữa sửa và thu cùng hộ).

## Chưa làm

Hai câu đọc thường giữ nguyên (`lapPhieu`, `DotCuTruDaoImpl.them`) đúng nhờ
thứ tự "khóa trước, đọc thường sau". Nếu sau này có ai thêm một câu đọc thường
trước khóa trong các giao dịch đó, lỗi cùng loại sẽ quay lại; quy tắc trong
architecture.md và chú thích ở các câu đó nhắc điều này. Chưa có test tự động
canh riêng cho hai chỗ này.
