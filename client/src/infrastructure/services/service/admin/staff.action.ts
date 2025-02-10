import {
    changeStatusStaff,
    createStaff,
    exportStaffs,
    exportTemplateStaffs,
    FindStaffRequest,
    getStaffById,
    getStaffs,
    importStaffs,
    StaffRequest,
    updateAvatarStaff,
    updateStaff
} from "@/infrastructure/services/api/admin/staff.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetStaffs = (
    params: Ref<FindStaffRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getStaffs>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.staff.staffList, params],
        queryFn: () => getStaffs(params),
        ...options,
    });
};

export const useCreateStaff = () => {
    const queryStaff = useQueryClient();
    return useMutation({
        mutationFn: (data: StaffRequest) => createStaff(data),
        onSuccess: () => {
            queryStaff.invalidateQueries({
                queryKey: [queryKey.admin.staff.staffList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.staff.staffList, "🚀 ~ staffCreate ~ error:", error);
        },
    });
};


export const useGetStaffById = (
    staffId: string, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getStaffById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.staff.staffDetail, staffId],
        queryFn: () => getStaffById(staffId),
        ...options,
    });
};


export const useUpdateStaff = () => {
    const queryStaff = useQueryClient();
    return useMutation({
        mutationFn: ({staffId, data,}: { staffId: string; data: StaffRequest; }) => updateStaff(staffId, data),
        onSuccess: () => {
            queryStaff.invalidateQueries({queryKey: [queryKey.admin.staff.staffList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.staff.staffList + "🚀 ~ staffUpdate ~ error:", error);
        },
    });
};

export const useChangeStatusStaff = () => {
    const queryStaff = useQueryClient();
    return useMutation({
        mutationFn: (staffId: string) => changeStatusStaff(staffId),
        onSuccess: () => {
            queryStaff.invalidateQueries({queryKey: [queryKey.admin.staff.staffList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.staff.staffList + "🚀 ~ staffDelete ~ error:", error);
        },
    });
};

export const useUpdateStaffAvatar = () => {
    const query = useQueryClient();
    return useMutation({
        mutationFn: ({staffId, data}: {
            staffId: string;
            data: StaffRequest;
        }) => updateAvatarStaff(staffId, data),
        onSuccess: () => {
            query.invalidateQueries({queryKey: [queryKey.admin.staff.staffDetail],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.staff.staffDetail + "🚀 ~ staffUpdate ~ error:", error);
        },
    });
}

export const useExportStaffs = () => {
    return useMutation({
        mutationFn: () => exportStaffs(),
        onSuccess: (res) => {
            const url = window.URL.createObjectURL(new Blob([res.data]));
            const excel = document.createElement("a");
            excel.href = url;
            excel.download = `nhan_vien_${Date.now()}.xlsx`;
            document.body.appendChild(excel);
            excel.click();
            excel.remove();
            window.URL.revokeObjectURL(url);
        },
        onError: (error: any) => {
            console.log(queryKey.admin.staff.exportStaffs + "🚀 ~ staffUpdateExportStaffs ~ error:", error);
        },
    });
};

export const useExportTemplateStaffs = () => {
    return useMutation({
        mutationFn: () => exportTemplateStaffs(),
        onSuccess: (res) => {
            const url = window.URL.createObjectURL(new Blob([res.data]));
            const excel = document.createElement("a");
            excel.href = url;
            excel.download = `nhan_vien_template${Date.now()}.xlsx`;
            document.body.appendChild(excel);
            excel.click();
            excel.remove();
            window.URL.revokeObjectURL(url);
        },
        onError: (error: any) => {
            console.log(queryKey.admin.staff.downloadTemplateStaffs + "🚀 ~ staffDownloadTemplateStaffs ~ error:", error);
        },
    });
};

export const useImportStaffs = () => {
    const queryStaff = useQueryClient();
    return useMutation({
        mutationFn: (data: FormData) => importStaffs(data),
        onSuccess: () => {
            queryStaff.invalidateQueries({queryKey: [queryKey.admin.staff.staffList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.staff.staffList + "🚀 ~ staffsImport ~ error:", error);
        },
    });
};
