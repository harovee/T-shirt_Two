import { ref, watch } from "vue";

export function useDebounce(value, delay = 500) {
    const debouncedValue = ref(value);

    watch(
        () => value,
        (newValue) => {
            const handler = setTimeout(() => {
                debouncedValue.value = newValue;
            }, delay);

            return () => {
                clearTimeout(handler);
            };
        },
        { immediate: true }
    );

    return debouncedValue;
}
