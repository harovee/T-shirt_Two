<template>
    <div class="flex flex-col items-center justify-center min-h-screen text-center">
      <img src="/src/assets/image/logo/T-ShirtTwo.png" width="400" />
      <h1 class="text-4xl">ĐẶT HÀNG THÀNH CÔNG</h1>
      <p class="max-w-lg px-4 text-xl">
        Chúc mừng quý khách đã thanh toán thành công. Nhân viên của chúng tôi sẽ liên hệ với quý khách khi đơn hàng được xác nhận.
        Quý khách cũng có thể theo dõi đơn hàng bằng cách đăng nhập và theo dõi đơn hàng trên website của chúng tôi.
      </p>
      <router-link to="/home">
        <a-button class="mt-5 px-6 py-2 text-lg h-12" type="primary" danger>
          TIẾP TỤC MUA SẮM
        </a-button>
      </router-link>
    </div>
</template>