<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { Radio, Table, Card , Row, Col, Statistic } from "ant-design-vue";
import * as echarts from "echarts";
import axios from "axios";
import dayjs from "dayjs";
import VChart from "vue-echarts";
import { use } from "echarts/core";
import { PieChart } from "echarts/charts";
import { CanvasRenderer } from "echarts/renderers";
import { TitleComponent, TooltipComponent, LegendComponent } from "echarts/components";

use([PieChart, CanvasRenderer, TitleComponent, TooltipComponent, LegendComponent]);


const timeUnitOptions = [
  { label: "Ngày", value: "day" },
  { label: "Tuần", value: "week" },
  { label: "Tháng", value: "month" },
  { label: "Năm", value: "year" }
];


const statisticTypeOptions = [
  { label: "Loại sản phẩm", value: "category" },
  { label: "Thương hiệu", value: "brand" },
  { label: "Loại hóa đơn", value: "orderType" },
  { label: "Nhân viên", value: "employee" },
];


const dateRange = ref([null, null]); // Mặc định không có ngày
const timeUnit = ref("day");
const statisticType = ref("employee");
const revenueData = ref([]);
const top5Products = ref([]);
const orderStatusData = ref([]);
const employeeStatistics = ref([]);
const categoryStatistics = ref([]);
const orderTypeStatistics = ref([]);
const summary = ref({ totalRevenue: 0, numberOrder: 0, numberProductSold: 0 });


const disabledDate = (current) => {
  return current && current.isAfter(dayjs(), 'day');
};
const disabledWeek = (current) => {
  return current && current.isAfter(dayjs(), 'week');
};
const disabledMonth = (current) => {
  return current && current.isAfter(dayjs(), 'month');
};
const disabledYear = (current) => {
  return current && current.isAfter(dayjs(), 'year');
};

const fetchRevenueData = async () => {
  // if (!dateRange.value.length) return;

  // const [start, end] = dateRange.value;
  // const { data } = await axios.post("/api/revenue", {
  //   statisticType: "REVENUE",
  //   timeUnit: timeUnit.value,
  //   startTime: start.valueOf(),
  //   endTime: end.valueOf()
  // });

  // revenueData.value = data;
  // summary.value = {
  //   totalRevenue: data.reduce((sum, d) => sum + d.totalRevenue, 0),
  //   numberOrder: data.reduce((sum, d) => sum + d.numberOrder, 0),
  //   numberProductSold: data.reduce((sum, d) => sum + d.numberProductSold, 0)
  // };

  // renderChart();
  // fetchAdditionalStats();
};

// const fetchAdditionalStats = async () => {
//   const [start, end] = dateRange.value;
//   const { data: products } = await axios.post("/api/top-products", { startTime: start.valueOf(), endTime: end.valueOf() });
//   top5Products.value = products;

//   const { data: orders } = await axios.post("/api/order-status", { startTime: start.valueOf(), endTime: end.valueOf() });
//   orderStatusData.value = orders;

//   const { data: employees } = await axios.post("/api/employee-statistics", { startTime: start.valueOf(), endTime: end.valueOf() });
//   employeeStatistics.value = employees;
// };

// const renderChart = () => {
//   const chartDom = document.getElementById("revenueChart");
//   if (!chartDom) return;
//   const chart = echarts.init(chartDom);
//   // chart.setOption({
//   //   title: { text: "Biểu đồ doanh thu" },
//   //   tooltip: { trigger: "axis" },
//   //   xAxis: { type: "category", data: revenueData.value.map(d => d.timeDisplay) },
//   //   yAxis: { type: "value" },
//   //   series: [{ name: "Doanh thu", type: "line", data: revenueData.value.map(d => d.totalRevenue) }]
//   // });

//   chart.setOption({
//     title: { text: 'Doanh thu theo thời gian' },
//     xAxis: { type: 'category', data: ['01/03', '02/03', '03/03', '04/03'] },
//     yAxis: { type: 'value' },
//     series: [{ data: [10000000, 12000000, 9000000, 15000000], type: 'line' }]
//   });
// };

// onMounted(fetchRevenueData);
// watch(revenueData, renderChart);



// Tham chiếu đến DOM để vẽ biểu đồ
const chartRef = ref(null);

const chartData = ref({
  categories: ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5"],
  values: [120, 200, 150, 80, 70], // Dữ liệu giả lập
});

onMounted(() => {
  const chartInstance = echarts.init(chartRef.value);

  const options = {
    title: {
      text: "Doanh thu theo ???",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
    },
    xAxis: {
      type: "category",
      data: chartData.value.categories,
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        name: "Doanh thu",
        type: "bar",
        data: chartData.value.values,
        itemStyle: {
          color: "#1890ff",
        },
      },
    ],
  };

  chartInstance.setOption(options);

  // Xử lý tự động resize khi thay đổi kích thước màn hình
  window.addEventListener("resize", () => chartInstance.resize());
});



const pieChartOptions = ref({
  tooltip: { trigger: "item" },
  legend: { top: "5%", left: "center" },
  series: [
    {
      name: "Trạng thái đơn hàng",
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


</script>

<template>
  <div class="dashboard-container">
    <div class="filter-section sticky-filter mt-2">
    <div class="text-lg">Thống kê theo thời gian tùy chỉnh:</div>
    <!-- Bộ lọc thời gian -->
    <a-range-picker 
      size="large"
      style="width: 100%;" 
      :disabled-date="disabledDate"
      :disabled-week="disabledWeek"
      :disabled-month="disabledMonth"
      :disabled-year="disabledYear"
      :picker="timeUnit"
      v-model:value="dateRange"
      :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']"
      @change="fetchRevenueData"
    />

    <!-- Chọn đơn vị thời gian -->
    <Radio.Group v-model:value="timeUnit" @change="fetchRevenueData" class="mt-2">
      <Radio v-for="option in timeUnitOptions" :key="option.value" :value="option.value">
        {{ option.label }}
      </Radio>
    </Radio.Group>
  </div>
    
    <div class="summary-cards mb-4  ">
      <Card><Statistic title="Tổng doanh thu" :value="summary.totalRevenue" suffix="VND" /></Card>
      <Card><Statistic title="Số đơn hàng" :value="summary.numberOrder" /></Card>
      <Card><Statistic title="Số sản phẩm bán ra" :value="summary.numberProductSold" /></Card>
    </div>

    <Row :gutter="16" class="chart-table-row mb-4">
      <Col :span="14">
        <Card title="Thống kê doanh thu">
          <div id="revenueChart" style="width: 800px; height: 400px;">
              <div ref="chartRef" style="width:100%; height: 400px"></div>
          </div>
        </Card>
      </Col>
      <Col :span="10">
        <Card title="Bảng dữ liệu">
          <Table :dataSource="revenueData" :columns="[
          { title: 'STT', dataIndex: 'timeDisplay' },
          { title: 'Thời gian', dataIndex: 'timeDisplay' },
          { title: 'Doanh thu', dataIndex: 'totalRevenue' },
          { title: 'Số đơn hàng', dataIndex: 'numberOrder' },
          { title: 'Số sản phẩm', dataIndex: 'numberProductSold' }
          ]" />
        </Card>
      </Col>
    </Row>


    <Row :gutter="16" class="chart-table-row mb-4">
      <Col :span="12">
        <Card title="Trạng thái hóa đơn">
          <VChart :option="pieChartOptions" style="height: 300px" />
        </Card>
      </Col>
      <Col :span="12">
        <Card title="Top 5 sản phẩm bán chạy">
          <Table :dataSource="top5Products" :columns="[
              { title: 'Sản phẩm', dataIndex: 'productName' },
              { title: 'Số lượng bán', dataIndex: 'quantitySold' },
              { title: 'Doanh thu', dataIndex: 'quantitySold' }
            ]" />
        </Card>
      </Col>
    </Row>

    


    <div>
        <h3 class="">Thống kê doanh thu theo: </h3>
        <Radio.Group v-model:value="statisticType" @change="fetchRevenueData">
            <Radio v-for="option in statisticTypeOptions" :key="option.value" :value="option.value">
              {{ option.label }}
        </Radio>
    </Radio.Group>
    </div>
    <Row :gutter="16" class="chart-table-row">
      <Col :span="12">
        <Card title="">
          <!-- <VChart :option="pieChartOptions" style="height: 300px" /> -->
        </Card>
      </Col>
      <Col :span="12">
        <Card :title="''">
          <Table :dataSource="employeeStatistics" :columns="[
              { title: 'Nhân viên', dataIndex: 'employeeName' },
              { title: 'Số hóa đơn', dataIndex: 'orderCount' },
              { title: 'Doanh thu', dataIndex: 'revenue' }
            ]" />
        </Card>
      </Col>
    </Row>
  </div>
</template>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.sticky-filter {
  position: sticky;
  top: 0;
  background: white;
  padding: 10px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}
.summary-cards {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  flex-wrap: wrap;
}
</style>
