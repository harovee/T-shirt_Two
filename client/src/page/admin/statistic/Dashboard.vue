<script setup lang="ts">
import { ref, h, onMounted, onUnmounted, computed } from "vue";
import { Card, Row, Col, Table, Tooltip } from "ant-design-vue";
import { ShoppingCartOutlined, DollarOutlined, UserAddOutlined } from "@ant-design/icons-vue";
import VChart from "vue-echarts";
import { use } from "echarts/core";
import { PieChart } from "echarts/charts";
import { CanvasRenderer } from "echarts/renderers";
import { TitleComponent, TooltipComponent, LegendComponent } from "echarts/components";
import axios from "axios";
import { PREFIX_API_ADMIN_STATISTIC } from "@/infrastructure/constants/url";
import OutStockProductTable from "./OutStockProductTable.vue";

// Đăng ký ECharts
use([PieChart, CanvasRenderer, TitleComponent, TooltipComponent, LegendComponent]);

const intervalId = ref<number | null>(null);


// Fake dữ liệu API
const todayStats = ref({
  timeDisplay: "01-03-2025", // Ngày thống kê
  totalOrders: 12, // Số đơn hàng hôm nay
  totalRevenue: 52000000, // Tổng doanh thu hôm nay
  newCustomers: 3, // Khách hàng mới hôm nay
});

// Biểu đồ trạng thái đơn hàng hôm nay
const pieChartOptions = ref({
  tooltip: { trigger: "item" },
  legend: { top: "5%", left: "center" },
  series: [
    {
      name: "Trạng thái đơn hàng hôm nay",
      type: "pie",
      radius: ["40%", "70%"],
      avoidLabelOverlap: false,
      label: { show: false, position: "center" },
      emphasis: { label: { show: true, fontSize: "18", fontWeight: "bold" } },
      data: [
        { value: 8, name: "Thành công", itemStyle: { color: "#28a745" } },
        { value: 3, name: "Chờ xử lý", itemStyle: { color: "#ffc107" } },
        { value: 1, name: "Đang giao", itemStyle: { color: "#007bff" } },
        { value: 0, name: "Hủy bỏ", itemStyle: { color: "#dc3545" } },
      ],
    },
  ],
});

// Dữ liệu top 5 sản phẩm bán chạy hôm nay
const topProducts = ref([
  { objectValue: "SP008 - Áo thun cổ tròn tay dài phong cách thể thao", numberProductSold: 5, numberOrder: 3, totalRevenue: 11000000 },
  { objectValue: "SP002 - Áo sơ mi nam công sở lịch lãm", numberProductSold: 3, numberOrder: 2, totalRevenue: 7500000 },
  { objectValue: "SP004 - Áo hoodie có mũ unisex thời trang", numberProductSold: 2, numberOrder: 2, totalRevenue: 5000000 },
  { objectValue: "SP003 - Áo khoác gió thể thao chống nước", numberProductSold: 2, numberOrder: 1, totalRevenue: 3200000 },
  { objectValue: "SP006 - Áo polo nam cao cấp phong cách Hàn Quốc", numberProductSold: 1, numberOrder: 1, totalRevenue: 2100000 },
]);



// Gọi API lấy dữ liệu
const fetchTodayStats = async () => {
  try {
    const response = await axios.get(`${PREFIX_API_ADMIN_STATISTIC}/today`);
    const data = response.data.data;
    // console.log(data);
    // Cập nhật todayStats
    const revenueData = data.revenues.data[0] || {};
    todayStats.value = {
      timeDisplay: revenueData.timeDisplay || "",
      totalOrders: revenueData.numberOrder || 0,
      totalRevenue: revenueData.totalRevenue || 0,
      newCustomers: data.numberNewCustomers || 0,
    };

    // Cập nhật trạng thái đơn hàng
    const orderStatus = data.numberOrderByStatus.data;
    pieChartOptions.value.series[0].data = [
      { value: orderStatus.numberSuccessOrder, name: orderStatus.numberSuccessOrder + " Thành công", itemStyle: { color: "#28a745" } },
      { value: orderStatus.numberWaitingOrder, name: orderStatus.numberWaitingOrder + " Chờ xử lý", itemStyle: { color: "#ffc107" } },
      { value: orderStatus.numberShippingOrder, name: orderStatus.numberShippingOrder + " Đang giao", itemStyle: { color: "#007bff" } },
      { value: orderStatus.numberCancelOrder, name:  orderStatus.numberCancelOrder + " Hủy bỏ", itemStyle: { color: "#dc3545" } },
    ];

    // Cập nhật top sản phẩm bán chạy
    topProducts.value = data.top10ProductBestSaleByRangeTime.data.map((item) => ({
      objectValue: item.objectValue,
      numberProductSold: item.numberProductSold,
      numberOrder: item.numberOrder,
      totalRevenue: item.totalRevenue,
    }));
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu thống kê hôm nay:", error);
  }
};


onMounted(() => {
  fetchTodayStats(); // Gọi ngay lần đầu
  intervalId.value = setInterval(fetchTodayStats, 60000); // Lặp lại mỗi 1 phút
});

onUnmounted(() => {
  if (intervalId.value) {
    clearInterval(intervalId.value); // Dừng lặp khi component bị hủy
  }
});


</script>

<template>
  <div class="dashboard-container">
    <!-- Tiêu đề + ngày -->
    <h2 class="dashboard-title">📊 Tổng quan - Ngày: <span class="date">{{ todayStats.timeDisplay }}</span></h2>

    <!-- Hàng Cards thống kê -->
    <Row :gutter="16">
      <Col :span="8">
        <Card class="stat-card stat-card-orders">
          <div class="stat-content">
            <ShoppingCartOutlined class="stat-icon" />
            <div>
              <p class="stat-label">Tổng đơn thành công</p>
              <p class="stat-value">{{ todayStats.totalOrders }}</p>
            </div>
          </div>
        </Card>
      </Col>
      <Col :span="8">
        <Card class="stat-card stat-card-revenue">
          <div class="stat-content">
            <DollarOutlined class="stat-icon" />
            <div>
              <p class="stat-label">Tổng doanh thu</p>
              <p class="stat-value">{{ todayStats.totalRevenue.toLocaleString() }} VND</p>
            </div>
          </div>
        </Card>
      </Col>
      <Col :span="8">
        <Card class="stat-card stat-card-customers">
          <div class="stat-content">
            <UserAddOutlined class="stat-icon" />
            <div>
              <p class="stat-label">Khách hàng mới</p>
              <p class="stat-value">{{ todayStats.newCustomers }}</p>
            </div>
          </div>
        </Card>
      </Col>
    </Row>

    <!-- Biểu đồ & Bảng -->
    <Row :gutter="16" class="chart-table-row">
      <Col :span="12">
        <Card title="Trạng thái đơn hàng hôm nay">
          <VChart :option="pieChartOptions" style="height: 300px" />
        </Card>
      </Col>
      <Col :span="12">
        <Card title="Top 5 sản phẩm bán chạy hôm nay">
          <Table :dataSource="topProducts" :pagination="false" :columns="[
            {
              title: 'Sản phẩm',
              dataIndex: 'objectValue',
              key: 'objectValue',
              customRender: ({ text }) => h(Tooltip, { title: text }, () => h('span', { class: 'ellipsis' }, text)),
            },
            { title: 'SL bán', dataIndex: 'numberProductSold', key: 'numberProductSold' },
            { title: 'Đơn hàng', dataIndex: 'numberOrder', key: 'numberOrder' },
            { title: 'Doanh thu (VND)', dataIndex: 'totalRevenue', key: 'totalRevenue' },
          ]" bordered />
        </Card>
      </Col>
    </Row>


    <Row :gutter="16" class="chart-table-row">
      <Col :span="12">
        <Card title="Sản phẩm sắp hết hàng">
          <OutStockProductTable ></OutStockProductTable>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.dashboard-title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
}
.date {
  color: #007bff;
  font-weight: bold;
}

/* Thiết kế các card thống kê */
.stat-card {
  border-radius: 10px;
  padding: 20px;
  transition: all 0.3s ease-in-out;
}
.stat-card-orders {
  background: #e3f2fd;
  border-left: 5px solid #007bff;
}
.stat-card-revenue {
  background: #e8f5e9;
  border-left: 5px solid #28a745;
}
.stat-card-customers {
  background: #fff3e0;
  border-left: 5px solid #ffa726;
}
.stat-card:hover {
  transform: translateY(-5px);
}

/* Căn giữa icon với số liệu */
.stat-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}
.stat-icon {
  font-size: 40px;
}
.stat-label {
  font-size: 14px;
  color: #555;
  margin-bottom: 2px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
}

/* Cắt chữ trong bảng và hiển thị tooltip */
.ellipsis {
  display: inline-block;
  width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Căn chỉnh biểu đồ & bảng */
.chart-table-row {
  margin-top: 20px;
}
</style>
