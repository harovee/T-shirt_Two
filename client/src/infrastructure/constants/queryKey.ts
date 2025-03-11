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
            }
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
            productDetailListNoPage: "adProdcutDetailListNoPageKey",
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
            billsWait: "adBillWaitKey"
        },
        billdetail: {
            detailList: "adBillDetail",
            billDetailById: "adBillDetailByIdKey",
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
            khachHangList: "adKhachHangListKey"
        },
    },
    client :{
        product: {
            productList: "clientAllProductKey",
            productDetailList: "clientProductDetailKey",
            danhMucList: "danhMucListKey",
            colorList: "colorListkey",
            thuongHieuList: "thuongHieuListKey",
            kieuDangList: "kieuDangListKey",
            chatLieuList: "chatLieuListKey"
        },
        payment: {
            invoiceOnline: "clientInvoiceOnlineKey"
        },
        myOrder: {
            clientBill: "clientBillList"
        }
    },
    user: {
        another: {},

    },

};

