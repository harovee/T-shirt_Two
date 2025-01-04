import { debounce } from "lodash";
import { ref, onMounted, onUnmounted, watch, Ref } from "vue";

export function useTableHeight(
  elementRef: Ref<HTMLElement | null>,
  minHeight: number = 0
) {
  const tableHeight = ref<number | undefined | string>(undefined);

  const resizeTable = debounce(
    () => {
      const node = elementRef.value;
      if (!node) return;

      const { top } = node.getBoundingClientRect();
      const windowHeight = window.innerHeight;

      const calculatedHeight = windowHeight - top - 48;
      tableHeight.value = Math.max(calculatedHeight, minHeight);
    },
    100,
    {
      trailing: true,
    }
  );

  onMounted(() => {
    resizeTable();
    window.addEventListener("resize", resizeTable);
  });

  onUnmounted(() => {
    window.removeEventListener("resize", resizeTable);
  });

  watch(elementRef, (newVal) => {
    if (newVal) {
      resizeTable();
    }
  });

  return tableHeight;
}
