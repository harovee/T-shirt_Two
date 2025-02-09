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
            orderDetails: "adPOSOrderDetailKey",
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
            allProductDetails: "adAllProdcutDetailListKey"
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
            detailList: "adBillDetail"
        },
        payment: {
            payHistory: "adPayHistory",
        },
        voucher: {
            voucherList: "adVoucherListKey",
            voucherDetail: "adVoucherDetailKey",
            khachHangList: "adKhachHangListKey"
        }
    },

    user: {
        another: {},

    },

};

