import {
    changeStatusSong,
    createGenre,
    createSong,
    FindSongRequest,
    GenreRequest,
    getGenres,
    getSong,
    getSongs,
    SongRequest,
    updateSong
} from "@/infrastructure/services/api/admin/song.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetSongs = (
    params: Ref<FindSongRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSongs>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.song.songList, params],
        queryFn: () => getSongs(params),
        ...options,
    });
};


export const useGetGenres = (options?: any): UseQueryReturnType<Awaited<ReturnType<typeof getGenres>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.song.genreList, options],
        queryFn: () => getGenres(),
        ...options,
    });
};

export const useCreateSong = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: SongRequest) => createSong(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.song.songList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.song.songList, "🚀 ~ songCreate ~ error:", error);
        },
    });
};


export const useGetSong = (
    songId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSong>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.song.songDetail, songId,],
        queryFn: () => getSong(songId),
        ...options,
    });
};


export const useUpdateSong = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({songId, data,}: { songId: string; data: SongRequest; }) => updateSong(songId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.song.songList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.song.songList + "🚀 ~ songUpdate ~ error:", error);
        },
    });
};

export const useChangeStatusSong = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (songId: string) => changeStatusSong(songId),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.song.songList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.song.songList + "🚀 ~ songDelete ~ error:", error);
        },
    });
};

export const useCreateGenre = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: GenreRequest) => createGenre(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.song.genreList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.song.genreList, "🚀 ~ genreCreate ~ error:", error);
        },
    });
};
