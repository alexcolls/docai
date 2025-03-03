<template>
  <div class="cursor-pointer">
    <USelectMenu
      v-model="selectedAI"
      icon="i-heroicons-cpu-chip-16-solid"
      :popper="{ placement: 'top' }"
      :selected="selectedAI"
      :options="aiList"
      class="px-1"
    />
  </div>
</template>

<script setup lang="ts">

const { app } = useStore();

const aiList = ref<string[]>([
  'GPT-4o',
  'Gemini-1.5'
]);

const selectedAI = ref<string>(aiList.value[app.selectedAiEngine]);

watch(selectedAI, () => {
  app.selectedAiEngine = aiList.value.indexOf(selectedAI.value);
  app.selectedAiEngineName = selectedAI.value;
});

</script>
