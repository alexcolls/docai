<template>
  <div class="w-full">
    <transition :name="transitionName" mode="out-in">
      <div
        v-if="currentPageContent"
        :key="currentPage"
        class="transition-container"
      >
        <div
          class="relative flex flex-wrap w-full p-2 border my-2
            rounded-lg border-gray-300 dark:border-gray-700"
        >
          <HomeChatMessageCol1Image
            :result-page="currentPageContent"
            :document="aiResponse.document"
            :total-pages="totalPages"
            :current-page="currentPage"
            @update:current-page="updatePage"
          />
          <HomeChatMessageCol2Json
            :response-id="aiResponse.id"
            :result-page="currentPageContent"
            :model="aiResponse.engine"
            class="pl-2"
          />
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">

const props = defineProps<{
  aiResponse: AiResponse
}>();

const currentPage = ref(1);
const totalPages = computed(() => props.aiResponse.resultPage.length);

const sortedPages = computed(() =>
  [...props.aiResponse.resultPage].sort((a, b) => a.page - b.page)
);

const currentPageContent = computed(
  () => sortedPages.value[currentPage.value - 1]);

const direction = ref<'forward' | 'backward'>('forward');

const updatePage = (newPage: number) => {
  direction.value = newPage > currentPage.value ? 'forward' : 'backward';
  currentPage.value = newPage;
};

const transitionName = computed(() =>
  direction.value === 'forward' ? 'slide-forward' : 'slide-backward'
);

</script>

<style scoped>
.slide-forward-enter-active,
.slide-forward-leave-active {
  transition: transform 0.5s ease;
}
.slide-forward-enter-from {
  transform: translateX(100%);
}
.slide-forward-enter-to {
  transform: translateX(0%);
}
.slide-forward-leave-from {
  transform: translateX(0%);
}
.slide-forward-leave-to {
  transform: translateX(-100%);
}
.slide-backward-enter-active,
.slide-backward-leave-active {
  transition: transform 0.5s ease;
}
.slide-backward-enter-from {
  transform: translateX(-100%);
}
.slide-backward-enter-to {
  transform: translateX(0%);
}
.slide-backward-leave-from {
  transform: translateX(0%);
}
.slide-backward-leave-to {
  transform: translateX(100%);
}
</style>
