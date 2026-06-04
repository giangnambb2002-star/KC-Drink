let cart = [];
let modal;

window.onload = function () {
    const modalElement = document.getElementById("sizeModal");
    if (modalElement) {
        modal = new bootstrap.Modal(modalElement);
    }
    loadCart();
};

function moModal(btn) {
    document.getElementById("spId").value = btn.dataset.id;
    document.getElementById("spTen").value = btn.dataset.ten;
    document.getElementById("spGia").value = btn.dataset.gia;

    document.querySelectorAll("input[name='size']").forEach(item => item.checked = false);
    document.querySelectorAll("input[name='toppings']").forEach(item => item.checked = false);

    modal.show();
}

function xacNhanThem() {
    const size = document.querySelector("input[name='size']:checked");
    if (!size) {
        alert("Vui lòng chọn size");
        return;
    }

    const giaGoc = parseFloat(document.getElementById("spGia").value);
    const phuThu = parseFloat(size.dataset.phuthu);

    let tongTienTopping = 0;
    let danhSachTenTopping = [];

    document.querySelectorAll("input[name='toppings']:checked").forEach(cb => {
        tongTienTopping += parseFloat(cb.dataset.gia);
        danhSachTenTopping.push(cb.dataset.ten);
    });

    const chuoiTopping = danhSachTenTopping.length > 0 ? danhSachTenTopping.join(", ") : "";
    const donGiaTong = giaGoc + phuThu + tongTienTopping;

    const item = {
        sanPhamId: parseInt(document.getElementById("spId").value),
        sizeId: parseInt(size.value),
        tenSanPham: document.getElementById("spTen").value,
        tenSize: size.dataset.ten,
        tenTopping: chuoiTopping,
        soLuong: 1,
        donGia: donGiaTong,
        thanhTien: donGiaTong
    };

    fetch('/ban-hang/them-gio', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(item)
    })
    .then(res => res.text())
    .then(data => {
        alert(data);
        modal.hide();
        loadCart();
    })
    .catch(err => console.error("Lỗi thêm giỏ hàng:", err));
}

function loadCart() {
    fetch('/ban-hang/gio-hang')
        .then(res => res.json())
        .then(data => {
            cart = data;
            renderCart();
        })
        .catch(err => console.error("Lỗi tải giỏ hàng:", err));
}

function renderCart() {
    let html = "";
    let tong = 0;

    cart.forEach((item, index) => {
        tong += Number(item.thanhTien);

        html += `
            <div class="cart-item" style="border-bottom: 1px solid #eee; padding: 12px 0;">
                <b style="color: #4a3319; font-size: 0.95rem;">${item.tenSanPham}</b>
                <div class="text-muted" style="font-size: 0.85rem; margin-top: 2px;">
                    <div><i class="fa-solid fa-mug-hot me-1"></i>Size: ${item.tenSize}</div>
                    <div style="color: #d47a13;"><i class="fa-solid fa-ice-cream me-1"></i>Topping: ${item.tenTopping ? item.tenTopping : 'Không có'}</div>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-2">
                    <div>
                        <button class="btn btn-sm btn-secondary py-0 px-2" onclick="giamSoLuong(${index})">-</button>
                        <span class="mx-2 fw-bold" style="font-size: 0.9rem;">${item.soLuong}</span>
                        <button class="btn btn-sm btn-success py-0 px-2" onclick="tangSoLuong(${index})">+</button>
                    </div>
                    <button class="btn btn-sm btn-danger py-1 px-2" onclick="xoaItem(${index})">
                        <i class="fa-solid fa-trash" style="font-size: 0.8rem;"></i>
                    </button>
                </div>
                <div class="text-end mt-1">
                    <b style="color: #333;">${Number(item.thanhTien).toLocaleString()} đ</b>
                </div>
            </div>
        `;
    });

    document.getElementById("cartContainer").innerHTML = html;
    document.getElementById("tamTinh").innerText = tong.toLocaleString() + " đ";
    document.getElementById("tongTien").innerText = tong.toLocaleString() + " đ";
}

function tangSoLuong(index) {
    fetch('/ban-hang/tang-sl/' + parseInt(index), { method: 'PUT' })
    .then(res => { if (res.ok) loadCart(); })
    .catch(err => console.error(err));
}

function giamSoLuong(index) {
    fetch('/ban-hang/giam-sl/' + parseInt(index), { method: 'PUT' })
    .then(res => { if (res.ok) loadCart(); })
    .catch(err => console.error(err));
}

function xoaItem(index) {
    if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này?")) {
        fetch('/ban-hang/xoa/' + parseInt(index), { method: 'DELETE' })
        .then(res => { if (res.ok) loadCart(); })
        .catch(err => console.error(err));
    }
}

function thanhToan() {

    if (cart.length === 0) {
        alert("Giỏ hàng trống!");
        return;
    }

    const request = {
        sdt: document.getElementById("soDienThoai").value,
        tenKhachHang: document.getElementById("tenKhachHang").value,
        ghiChu: document.getElementById("ghiChu").value
    };

    fetch('/ban-hang/thanh-toan', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
    })
    .then(res => res.text())
    .then(data => {
        alert(data);
        loadCart();
    });
}
function timKhachHang() {

    const sdt = document.getElementById("soDienThoai").value;

    if (!sdt) return;

    fetch('/khach-hang/tim?sdt=' + encodeURIComponent(sdt))
        .then(response => response.text())
        .then(text => {

            console.log("Response:", text);

            const tenKhachHang =
                document.getElementById("tenKhachHang");

            if (!text || text.trim() === "") {

                tenKhachHang.value = "";
                tenKhachHang.readOnly = false;
                return;
            }

            const data = JSON.parse(text);

            tenKhachHang.value = data.tenKhachHang;
            tenKhachHang.readOnly = true;
        })
        .catch(error => {
            console.error(error);
        });
}

console.log("ban-hang.js loaded");