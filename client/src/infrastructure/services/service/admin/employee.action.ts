import {
    changeStatusEmployee,
    createEmployee,
    FindEmployeeRequest,
    getEmployee,
    getEmployees,
    EmployeeRequest,
    updateEmployee
} from "@/infrastructure/services/api/admin/employee.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetEmployees = (
    params: Ref<FindEmployeeRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getEmployees>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.employee.employeeList, params],
        queryFn: () => getEmployees(params),
        ...options,
    });
};

export const useCreateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: EmployeeRequest) => createEmployee(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.employee.employeeList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.employee.employeeList, "🚀 ~ employeeCreate ~ error:", error);
        },
    });
};


export const useGetEmployee = (
    employeeId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getEmployee>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.employee.employeeDetail, employeeId,],
        queryFn: () => getEmployee(employeeId),
        ...options,
    });
};


export const useUpdateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({employeeId, data,}: { employeeId: string; data: EmployeeRequest; }) => updateEmployee(employeeId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.employee.employeeList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.employee.employeeList + "🚀 ~ employeeUpdate ~ error:", error);
        },
    });
};

export const useChangeStatusEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (employeeId: string) => changeStatusEmployee(employeeId),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.employee.employeeList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.employee.employeeList + "🚀 ~ employeeDelete ~ error:", error);
        },
    });
};