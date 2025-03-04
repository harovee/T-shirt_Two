<template>
    <div class="container mx-auto pt-10">
        <h4 class="breadcrumb-title">
        <router-link to="/home" class="breadcrumb-link">Giỏ hàng</router-link>
      </h4>
    </div>
</template>

<style scoped>
.breadcrumb-link {
color: #1890ff;
text-decoration: none;
transition: color 0.2s ease-in-out;
}

.breadcrumb-link:hover {
color: #0056b3;
text-decoration: underline;
}

.breadcrumb-title {
font-size: 16px;
font-weight: 500;
color: #555;
margin-bottom: 20px;
}
</style>