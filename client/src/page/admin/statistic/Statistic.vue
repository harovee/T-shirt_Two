<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from "vue";
import { Radio, Table, Card , Row, Col, Statistic } from "ant-design-vue";
import * as echarts from "echarts";
import dayjs, { Dayjs, OpUnitType } from "dayjs";
import VChart from "vue-echarts";
import { use } from "echarts/core";
import { PieChart } from "echarts/charts";
import { CanvasRenderer } from "echarts/renderers";
import { TitleComponent, TooltipComponent, LegendComponent } from "echarts/components";
import { NumberOrderByStatusResponse, RevenueRequest, RevenueResponse } from "@/infrastructure/services/api/admin/statistic.api";
import { useGetStatisticData } from "@/infrastructure/services/service/admin/statistic.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { upperCase } from "lodash";

use([PieChart, CanvasRenderer, TitleComponent, TooltipComponent, LegendComponent]);

type RangeValue = [Dayjs, Dayjs] | null;

const timeUnitOptions = [
  { label: "Ngày", value: "day" },
  { label: "Tuần", value: "week" },
  { label: "Tháng", value: "month" },
  { label: "Năm", value: "year" }
];


const statisticTypeOptions = [
  // { label: "Loại sản phẩm", value: "category" },
  // { label: "Thương hiệu", value: "brand" },
  { label: "Loại hóa đơn", value: "order_type" },
  { label: "Nhân viên", value: "employee" },
];


const dateRange = ref<RangeValue>(null); // Mặc định không có ngày
const placeholderDateRangePicker = ref(['Ngày bắt đầu', 'Ngày kết thúc']);
const timeUnit = ref("day");
const statisticType = ref("employee");
const revenueData = ref([]);
const top5Products = ref([]);
const statisticByTypeData = ref([]);
const statisticByTypeTableColums = ref([
              { title: 'Nhân viên', dataIndex: 'objectValue' },
              { title: 'Số hóa đơn', dataIndex: 'numberOrder' },
              { title: 'Doanh thu', dataIndex: 'totalRevenue' }
            ])
const summary = ref({ totalRevenue: 0, numberOrder: 0, numberProductSold: 0 });


const revenueParams = ref<RevenueRequest>({
  page: 1,
  size: 5,
  statisticType: "ALL",
  timeUnit: "DAY",
  startTime: null,
  endTime: null
});

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
const onRangeChange = (dates: RangeValue, dateStrings: string[]) => {
  if (!dates) {
    revenueParams.value['startTime'] = null;
    revenueParams.value['endTime'] = null;
    dateRange.value = null;
    return;
  }

  dateRange.value = dates;
  revenueParams.value["startTime"] = dates[0].valueOf();
  revenueParams.value["endTime"] = dayjs(dates[1]).endOf(timeUnit.value as OpUnitType).valueOf();
};

interface ChartData {
  categories: string[];
  values: number[];
}

const chartRef = ref<HTMLDivElement | null>(null); // Định nghĩa kiểu dữ liệu cho chartRef
const chartInstance = ref<echarts.ECharts | null>(null); // Định nghĩa kiểu dữ liệu cho ECharts instance
const chartData = ref<ChartData>({
  categories: ["Tháng 1", "Tháng 2", "Tháng 3"],
  values: [100, 200, 150],
});

onMounted(() => {
  if (!chartRef.value) return; // Đảm bảo chartRef không null
  chartInstance.value = echarts.init(chartRef.value);
  updateChart(); // Khởi tạo biểu đồ với dữ liệu ban đầu

  const resizeObserver = new ResizeObserver(() => {
    if (chartInstance.value) {
      chartInstance.value.resize();
    }
  });

  resizeObserver.observe(chartRef.value);
});

// Hàm cập nhật biểu đồ
const updateChart = () => {
  if (!chartInstance.value) return;

  const options: echarts.EChartsOption = {
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
    grid: {
      left: "10%",  // Canh trái
      right: "10%", // Canh phải
      bottom: "15%", // Canh dưới
      containLabel: true, // Giữ nhãn không bị tràn
    },
    series: [
      {
        name: "Doanh thu",
        type: "bar",
        data: chartData.value.values,
        barWidth: "40%",
        itemStyle: {
          color: "#1890ff",
        },
      },
    ],
  };

  chartInstance.value.setOption(options);
};

watch(chartData, () => {
  nextTick(updateChart); // Cập nhật chart sau khi DOM cập nhật
}, { deep: true });




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

const {data, isLoading, isFetching} = useGetStatisticData(revenueParams, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

//map chart

function mapStatisticData(data : Array<RevenueResponse> | undefined){
  if (data) {
      let totalRevenue = 0;
      let totalProductSold = 0;
      let totalOrderCount = 0;

      let chartCategories = [];
      let chartValue = [];

      data.forEach((e) => {
        totalRevenue += e.totalRevenue || 0;
        totalProductSold += e.numberProductSold || 0;
        totalOrderCount += e.numberOrder || 0;
        chartCategories.push(e.timeDisplay as never);
        chartValue.push(e.totalRevenue as never);
      });

      summary.value.totalRevenue = totalRevenue;
      summary.value.numberProductSold = totalProductSold;
      summary.value.numberOrder = totalOrderCount;

      chartData.value.categories = chartCategories;
      chartData.value.values = chartValue;

  }
  
}

watch(
  () => statisticType.value,
    (newStatisticType) => {
      if (newStatisticType) {
          // console.log(newStatisticType)
          statisticByTypeTableColums.value[0].title =
          statisticTypeOptions.find(e => e.value === newStatisticType)?.label as string;
          revenueParams.value.statisticType = newStatisticType;
      }
    },
    {immediate: true}
);

watch(
  () => timeUnit.value,
    (newTimeUnitValue) => {
      if (newTimeUnitValue) {
          // console.log(newTimeUnitValue)
          placeholderDateRangePicker.value = 
              newTimeUnitValue === 'day' ? ['Ngày bắt đầu', 'Ngày kết thúc'] :
              newTimeUnitValue === 'week' ? ['Tuần bắt đầu', 'Tuần kết thúc'] :
              newTimeUnitValue === 'month' ? ['Tháng bắt đầu', 'Tháng kết thúc'] : ['Năm bắt đầu', 'Năm kết thúc'] ;
          revenueParams.value.timeUnit = upperCase(newTimeUnitValue);
      }
    },
    {immediate: true}
);

watch(
  () => data.value,
    (newStatisticData) => {
      if (newStatisticData) {
          // console.log(newStatisticData)

          mapStatisticData(newStatisticData.data.revenues?.data);

          revenueData.value = newStatisticData.data.revenues?.data as never[];
          statisticByTypeData.value = newStatisticData.data.revenuesType?.data as never[];
          top5Products.value = newStatisticData.data.top10ProductBestSaleByRangeTime?.data as never[];
          
          const orderStatus = newStatisticData.data.numberOrderByStatus?.data as NumberOrderByStatusResponse;
          pieChartOptions.value.series[0].data = [
              { value: orderStatus.numberSuccessOrder, name: orderStatus.numberSuccessOrder + " Thành công", itemStyle: { color: "#28a745" } },
              { value: orderStatus.numberWaitingOrder, name: orderStatus.numberWaitingOrder + " Chờ xử lý", itemStyle: { color: "#ffc107" } },
              { value: orderStatus.numberShippingOrder, name: orderStatus.numberShippingOrder + " Đang giao", itemStyle: { color: "#007bff" } },
              { value: orderStatus.numberCancelOrder, name:  orderStatus.numberCancelOrder + " Hủy bỏ", itemStyle: { color: "#dc3545" } },
            ];
          
      }
    },
    { deep: true }
);





</script>

<template>
  <div class="dashboard-container">
      <div class="filter-section sticky-filter mt-2">
          <Row :gutter="6">
              <!-- Bộ lọc thời gian -->
              <Col :span="11">
                  <div class="text-lg">Thống kê theo thời gian tùy chỉnh:</div>
                  <a-range-picker 
                    size="large"
                    style="width: 100%;" 
                    :disabled-date="disabledDate"
                    :disabled-week="disabledWeek"
                    :disabled-month="disabledMonth"
                    :disabled-year="disabledYear"
                    :picker="timeUnit"
                    v-model:value="dateRange"
                    :placeholder="placeholderDateRangePicker"
                    @change="onRangeChange"
                  />

                  <!-- Chọn đơn vị thời gian -->
                  <Radio.Group v-model:value="timeUnit" class="mt-2">
                    <Radio v-for="option in timeUnitOptions" :key="option.value" :value="option.value">
                      {{ option.label }}
                    </Radio>
                  </Radio.Group>
              </Col>

              <Col :span="13">
                <div class="summary-cards">
                  <Card><Statistic title="Tổng doanh thu" :value="summary.totalRevenue" suffix="đ" /></Card>
                  <Card><Statistic title="Số đơn hàng" :value="summary.numberOrder" /></Card>
                  <Card><Statistic title="Số sản phẩm bán ra" :value="summary.numberProductSold" /></Card>
                </div>
              </Col>
            </Row>
          
    </div>
    
    

    <Row :gutter="16" class="chart-table-row my-4">
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
          { title: 'STT', dataIndex: 'index', customRender: ({ index }) => index + 1},
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
        <Card title="Trạng thái hóa đơn"  style="height: 500px;">
          <VChart :option="pieChartOptions" style="height: 400px" />
        </Card>
      </Col>
      <Col :span="12">
        <Card title="Top 5 sản phẩm bán chạy"  style="height: 500px;">
          <Table :dataSource="top5Products" :pagination="false" :columns="[
              { title: 'Sản phẩm', dataIndex: 'objectValue' },
              { title: 'Số lượng bán', dataIndex: 'numberProductSold' },
              { title: 'Doanh thu', dataIndex: 'totalRevenue' }
            ]" />
        </Card>
      </Col>
    </Row>

    


    <div>
        <h3 class="">Thống kê doanh thu theo: </h3>
        <Radio.Group v-model:value="statisticType" >
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
        <Card :title="''"  style="height: 400px;">
          <Table :dataSource="statisticByTypeData" :pagination="false" :columns="statisticByTypeTableColums" />
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
  justify-content: end;
}
</style>
