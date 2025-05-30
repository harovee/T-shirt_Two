import { notification } from "ant-design-vue";

export const queryKey = {
    common: {
        upload: {
            audio: "cmAudioKey"
        }
    },
    authentication: {
        login: "loginKey",
        logout: "logoutKey",
        register: "registerKey"
    },
    admin: {
        statistic: {
            detailData: "detailDataKey",
            commonRevenueList: "commonRevenueListKey",
            outStockProductList: "adOutStockProductListKey",
        },
        pointOfSale: {
            products: "adPOSproductsKey",
            orderDetails: "adPOSOrderDetailKey"
        },
        client: {
            clientList: "adClientListKey",
            clientDetail: "adClientDetailKey",
            address: {
                addressList: "adClientAddressListKey",
                province: "adClientAddressProvince",
                district: "adClientAddressDistrict",
                districtM: "adClientAddressDistrictM",
                ward: "adClientAddressWard",
                wardM: "adClientAddressWardM",
            },
            clientChatList: "adClientChatListKey",
        },
        staff: {
            staffList: "adStaffListKey",
            staffDetail: "adStaffDetailKey",
            exportStaffs: "adStaffExportKey",
            importStaffs: "adStaffImportKey",
            downloadTemplateStaffs: "adStaffDownloadTemplateKey",
        },
        user: {
            userList: "adUserListKey",
            userDetail: "adUserDetailKey",
        },
        song: {
            songList: "adSongListKey",
            songDetail: "adSongDetailKey",
            genreList: "adGenresListKey",
        },
        sale: {
            saleList: "adSaleListKey",
            songDetail: "adSaleDetailKey",
            productList: "adProductInSaleMdListKey",
            productDetailList: "adProductDetailInSaleMdListKey",
            attributeList: "adAttributeListKey",
            saleProductList: "adSaleProductListKey"
        },
        product: {
            productList: "adProdcutListKey",
        },
        productDetail: {
            productDetailList: "adProdcutDetailListKey",
            allProductDetails: "adAllProdcutDetailListKey",
            allProductDetailOverZero: "adAllProdcutDetailOverZeroKey",
            productDetailListNoPage: "adProdcutDetailListNoPageKey",
            checkQuantity: "adCheckQuantity"
        },
        category: {
            categoryList: "adCategoryListKey",
        },
        material: {
            materialList: "adMaterialListKey",
        },
        collar: {
            collarList: "adCollarListKey",
        },
        pattern: {
            patternList: "adPatternListKey",
        },
        size: {
            sizeList: "adSizeListKey",
        },
        color: {
            colorList: "adColorListKey",
        },
        style: {
            styleList: "adStyleListKey",
        },
        sleeve: {
            sleeveList: "adSleeveListKey",
        },
        trademark: {
            trademarkList: "adTrademarkListKey",
        },
        feature: {
            featureList: "adFeatureListKey",
        },
        bill: {
            billList: "adBillListKey",
            billById: "adBillByIdKey",
            billHistory: "adBillHistoryKey",
            billsWait: "adBillWaitKey",
            billRefund: "adBillRefundKey",
        },
        billdetail: {
            detailList: "adBillDetail",
            billDetailById: "adBillDetailByIdKey",
            billDetailByMaHD: "adBillDetailByMaHDKey"
        },
        payment: {
            payHistory: "adPayHistory",
            paymentMethod: "adPaymentMethodKey",
            deliveryPayment: "adDeliveryPaymentKey",
            customerList: "adCustomerListKey",
            customerDetail: "adCustomerDetailKey",
            voucherPayList: "voucherPayListKey",
            voucherPayDetail: "voucherPayDetailKey",
            voucherByCode: "voucherByCode",
            customerAddress: "adCustomerAddress",
            nextPriceVoucher: "adNextPriceVoucher",
            shippingFee: "shippingFeeKey",
            paymentMethodDetail: "adPaymentMethodDetail",
            serviceId: "serviceIdKey"
        },
        voucher: {
            voucherList: "adVoucherListKey",
            voucherDetail: "adVoucherDetailKey",
            khachHangList: "adKhachHangListKey",
            voucherUse: "adVoucherInUse"
        },
        livechat: {
            chatHistory: "adChatHistoryKey",
        }
    },
    client :{
        product: {
            productList: "clientAllProductKey",
            productDetailList: "clientProductDetailKey",
            productDetailWithColor: "clientProductDetailWithColorKey",
            productDetailWithSize: "clientProductDetailWithSizeKey",
            danhMucList: "danhMucListKey",
            colorList: "colorListkey",
            thuongHieuList: "thuongHieuListKey",
            kieuDangList: "kieuDangListKey",
            chatLieuList: "chatLieuListKey",
            productSaleList: "clientSaleProductKey",
            productBestSaleList: "clientBestSaleProductKey",
        },
        payment: {
            invoiceOnline: "clientInvoiceOnlineKey",
            invoiceOnlineVNPay:"clientInvoiceOnlineVNpayKey",
            invoiceOnlineMomo:"clientInvoiceOnlineMomoKey",
            invoiceInlineVietQr: "clientInvoiceOnlineVieQrKey"
        },
        myOrder: {
            clientBill: "clientBillList"
        }
    },
    user: {
        another: {},

    },
    websocket: {
        notification: {
            notification: "notificationKey"
        }
    }
};

