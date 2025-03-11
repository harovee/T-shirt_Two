<template>
  <div class="container mx-auto pt-10">
    <h1
      class="flex items-center justify-center text-center gap-4 text-4xl font-bold mb-10 text-red-700"
    >
      <v-icon name="md-shoppingbag-sharp" style="width: 35px; height: 35px" />
      ĐẶT HÀNG
    </h1>
    <div>
      <a-row :gutter="[8, 24]">
        <a-col :span="16">
          <div class="bg-gray-100 p-8">
            <payment-info-address
            @handleGetAddress="handleGetAddressCustomer"
             />
          </div>
          <div class="bg-gray-100 mt-5 mb-5 p-5">
            <cart-table-in-payment
              :dataSource="listProducts"
            />
          </div>
        </a-col>
        <a-col :span="8">
          <payment-infomation
          :dataAddress="info"
          :totalPrice="paymentInfo.totalProductPrice"
          :shippingFee="paymentInfo.shippingFee"
          :fullAddressCustomer="fullAddress"
          :validateAddress="validateAddress"
          :memo="ghiChu"
          />
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useCartStore } from "@/infrastructure/stores/cart";
import { computed, onMounted, ref, watch } from "vue";
import PaymentInfoAddress from "./PaymentInfoAddress.vue";
import CartTableInPayment from "@/page/client/cart/CartTableInPayment.vue"
import PaymentInfomation from "./PaymentInfomation.vue"
import { log } from "console";
import {
  VoucherResponse,
  FindVoucherRequest,
  nextVoucherRequest,
  ShippingFeeRequest,
  getWardByCode,
  getDistrictById,
  getProvinceById,
  ServiceIdRequest,
  createInvoicePdf,
} from "@/infrastructure/services/api/admin/payment.api";
import {
  useGetListVoucher,
  useGetVoucherById,
  useGetPriceNextVoucher,
  useGetShippingFee,
  useGetServiceId,
} from "@/infrastructure/services/service/admin/payment.action";
import { keepPreviousData } from "@tanstack/vue-query";

const cartStore = useCartStore();

const listProducts = computed(() => cartStore.checkoutData);

const totalPrice = computed(() => {
  return listProducts.value.reduce((total, item) => total + item.gia * item.soLuong, 0);
});

const fullAddress = ref(null);

const info = ref(null);

const ghiChu = ref("")

const validateAddress = ref(false)

const paymentInfo = ref({
  method: "cash",
  voucherCode: "",
  voucherId: null,
  shippingOption: "false",
  shippingFee: 0,
  discount: 0,
  total: 0,
  totalProductPrice: totalPrice.value,
  name: "" || null,
  fullAddress: "" || null,
  phoneNumber: "" || null,
});

const handleGetAddressCustomer = (modelRef: any, fullAddressRef: string, check: boolean) => {
  fullAddress.value = fullAddressRef;
  info.value = modelRef;
  ghiChu.value = modelRef.ghiChu;
  serviceIdParams.value.formDistrict = shippingParams.value.fromDistrictId;
  serviceIdParams.value.toDistrict = Number(modelRef.district);
  if(check) {
    validateAddress.value = true
  } else {
    validateAddress.value = false
  }
}

const calculateProductDimensions = () => {
  const totalWeight = listProducts.value.reduce((sum: any, product: any) => {
    return sum + product.soLuong * 200;
  }, 0);
  const totalHeight = listProducts.value.reduce((sum, product) => {
    return sum + product.soLuong * 3;
  }, 0);
  return {
    weight: totalWeight,
    length: 30,
    width: 20,
    height: totalHeight,
  };
};

const shippingParams = computed<ShippingFeeRequest>(() => ({
  fromDistrictId: 3440,
  fromWardCode: "13007",
  toDistrictId: "",
  toWardCode: "",
  serviceId: 0,
  ...calculateProductDimensions(),
}));

const serviceIdParams = ref<ServiceIdRequest>({
  formDistrict: 0,
  toDistrict: 0,
  shopId: 2509559,
});

// Lấy service ID GHN
const { data: service, refetch: refetchService } = useGetServiceId(
  serviceIdParams,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled:
      !!serviceIdParams.value.formDistrict &&
      !!serviceIdParams.value.toDistrict,
  }
);

const { data: shipping, refetch: refetchShipping } = useGetShippingFee(
  shippingParams,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled:
      !!shippingParams.value.toDistrictId &&
      !!shippingParams.value.toWardCode,
  }
);

watch(
  () => info.value,
  (newData) => {
    if (newData) {
      if (serviceIdParams.value.toDistrict !== 0) {
        refetchService().then(() => {
          shippingParams.value.serviceId = service?.value?.data[0].service_id;
          shippingParams.value.toDistrictId = info.value.district;
          shippingParams.value.toWardCode = info.value.ward;
          if (shippingParams.value.toWardCode) {
            refetchShipping().then(() => {
              paymentInfo.value.shippingFee = shipping?.value?.data.total;
            });
          }
        });
      } else {
        paymentInfo.value.shippingFee = 0;
      }
    }
  }, {deep: true}
);

// watch(() => totalPrice.value , (newData) => {
//   paymentInfo.value.totalProductPrice = newData
//   console.log(paymentInfo.value);
// }) 

</script>

<style scoped>
</style>