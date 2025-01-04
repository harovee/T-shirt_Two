import {DefaultResponse} from "@/infrastructure/types/api.common";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_COMMON} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface AudioUploadRequest {
    file: File
}

export type AudioUploadResponse = {
    id: string,
    fileName: string,
    fileType: string,
    [key: string]: any;
}

export const uploadAudio = async (data: AudioUploadRequest) => {
    const formData = new FormData();
    formData.append("file", data.file);
    const res = (await request({
        url: `${PREFIX_API_COMMON}/upload`,
        method: "POST",
        data: formData
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<AudioUploadResponse | Object>>
    >;

    return res.data;
};