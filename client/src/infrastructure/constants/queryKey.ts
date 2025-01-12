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
        client: {
            clientList: "adClientListKey",
            clientDetail: "adClientDetailKey",
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
        bill: {
            billList: "adBillListKey",
        },
        billdetail: {
            detailList: "adBillDetail"
        }

    },

    user: {
        another: {},

    },

};

