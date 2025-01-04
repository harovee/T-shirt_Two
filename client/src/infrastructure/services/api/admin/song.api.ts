import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_SONG} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertySongParams {
    keyword?: string | null;
    status?: number | null;
    genre?: string | null;

    [key: string]: any;
}

export interface FindSongRequest extends PropertySongParams, PaginationParams {

}

export interface SongRequest {
    title: string | null;
    artist: string | null;
    releasedDate: number | null;
    genre: string[] | undefined;
    audioFile: string | null;
}

export type SongResponse = ResponseList & {
    title: string;
    artist: string;
    releasedDate: number;
    profilePicture: string;
    status: number;
};

export type DetailSongResponse = {
    title: string;
    artist: string;
    duration: number;
    audioFile: number[];
};

export interface GenreRequest {
    genre: string[] | undefined;
}

export type GenreResponse = {
    id: string;
    name: string;
    description: string;
}


export const getSongs = async (params: Ref<FindSongRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SONG}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<SongResponse>>>
    >;

    return res.data;
};

export const getGenres = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SONG}/genre`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<Array<GenreResponse>>
    >;

    return res.data;
}

export const createSong = async (data: SongRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SONG}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getSong = async (songId: Ref<string | null>) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SONG}/${songId}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<SongResponse>>>
    >;
};

export const updateSong = async (songId: string, data: SongRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SONG}/${songId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const changeStatusSong = async (songId: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SONG}/${songId}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const createGenre = async (data: GenreRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SONG}/genre`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
}