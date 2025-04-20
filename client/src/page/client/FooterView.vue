<template>
  <div class="container mx-auto">
    <div class="mb-7 grid grid-cols-11 gap-6">
      <div class="col-span-9">
        <table border="none" class="w-full">
          <thead>
            <tr>
              <th>Về T-shirt Two</th>
              <th>Hỗ trợ khách hàng</th>
              <th>Chính sách</th>
              <th>Liên hệ</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><a href="#">Chúng tôi là ai</a></td>
              <td><a href="#">Hướng dẫn đặt hàng</a></td>
              <td><a href="#">Chính sách vận chuyển</a></td>
              <td>Tư vấn mua online: 024 7308 2882</td>
            </tr>
            <tr>
              <td><a href="#">Cam kết của chúng tôi</a></td>
              <td><a href="#">Phương thức thanh toán</a></td>
              <td><a href="#">Chính sách kiểm hàng</a></td>
              <td>Khiếu nại và bảo hành: 024 7300 6999</td>
            </tr>
            <tr>
              <td><a href="#">Tin tuyển dụng</a></td>
              <td><a href="#">Chính sách thành viên</a></td>
              <td><a href="#">Chính sách đổi trả</a></td>
              <td>Email: tshirttwo2@gmail.com</td>
            </tr>
            <tr>
              <td></td>
              <td></td>
              <td><a href="#">Điều kiện/Điều khoản</a></td>
              <td>Giờ làm việc: 8:30 - 22:00 hàng ngày</td>
            </tr>
            <tr>
              <td></td>
              <td></td>
              <td><a href="#">Chính sách bảo mật</a></td>
              <td></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="col-span-2">
        <h1 class="font-bold">Mạng xã hội</h1>
        <div class="flex items-center">
          <a-button
            class="border border-gray-300 me-3  w-10 h-10 flex justify-center items-center"
            type="text"
            ><v-icon name="co-facebook-f" class="w-8 h-8"
          /></a-button>
          <a-button
            class="border border-gray-300 me-3 w-10 h-10 flex justify-center items-center"
            type="text"
            ><v-icon name="si-zalo" class="w-8 h-8"
          /></a-button>
          <a-button
            class="border border-gray-300 me-3 w-10 h-10 flex justify-center items-center"
            type="text"
            ><v-icon name="fa-instagram" class="w-8 h-8"
          /></a-button>
          <a-button
            class="border border-gray-300 w-10 h-10 flex justify-center items-center"
            type="text"
            ><v-icon name="bi-telephone-inbound-fill" class="w-8 h-8"
          /></a-button>
        </div>
      </div>
    </div>
    <hr />
    <div class="mt-7">
      <h2>Công ty TNHH T-shirt Two</h2>
      <br />
      <p>
        Địa chỉ trụ sở doanh nghiệp: Tầng 26, Tòa nhà L, 285 Trường cao đẳng FPT
        Polytechnic, đường Trịnh Văn Bô, quận Nam Từ Liêm, thành phố Hà Nội
      </p>
      <p>Điện thoại: 0987654321</p>
      <p>Email: tshirttwo2@gmail.com</p>
    </div>
  </div>
</template>
<style scoped>
th {
  text-align: left;
}

table {
  border-spacing: 10px; /* Điều chỉnh khoảng cách giữa các dòng và cột */
}

th,
td {
  padding-top: 10px;
  padding-right: 10px;
  padding-bottom: 10px; /* Điều chỉnh khoảng cách bên trong các ô */
}
</style>