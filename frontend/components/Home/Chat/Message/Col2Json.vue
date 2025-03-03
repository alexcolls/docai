<template>
  <div class="relative rounded-lg text-sm w-2/3">
    <div v-if="resultPage?.status === 'DONE'">
      <div class="absolute top-4 right-4">
        <ModalDeleteChatMessage />
        <ModalAddSample />
        <UDropdown
          :items="rowMenu(responseId)"
          :ui="{
            base: 'w-[120px] bg-gray-100'
          }"
          :popper="{
            placement: 'bottom'
          }"
        >
          <UButton
            color="gray"
            variant="ghost"
            icon="i-heroicons-ellipsis-vertical-20-solid"
            class="active:bg-white dark:active:bg-gray-900 border
              active:shadow-inner shadow-gray-300 dark:shadow-gray-700 z-40
              bg-gray-100 dark:bg-gray-800 border-gray-200 dark:border-gray-700
              hover:dark:bg-gray-800/50 hover:bg-gray-100/50"
          />
        </UDropdown>
      </div>
      <CommonJsonViewerColors
        :json-data="(safeParseJSON(resultPage?.response) as JSON) ??
          resultPage?.response"
        class="rounded-lg mt-[4px] opacity-90 border border-primary/20"
      />
    </div>
    <div
      v-else-if="!resultPage.status || resultPage?.status === 'PROCESSING'"
      class="relative p-4 bg-white dark:bg-gray-900 rounded-lg
        mt-[2px] opacity-60 border border-primary/20"
    >
      <div class="absolute top-4 right-4">
        <ModalDeleteChatMessage />
        <ModalAddSample />
        <UDropdown
          :items="[rowMenu(responseId)[1]]"
          :ui="{
            base: 'w-[120px] bg-gray-100'
          }"
          :popper="{
            placement: 'bottom'
          }"
        >
          <UButton
            color="gray"
            variant="ghost"
            icon="i-heroicons-ellipsis-vertical-20-solid"
            class="active:bg-white dark:active:bg-gray-900 border
              active:shadow-inner shadow-gray-300 dark:shadow-gray-700 z-40
              bg-gray-100 dark:bg-gray-800 border-gray-200 dark:border-gray-700
              hover:dark:bg-gray-800/50 hover:bg-gray-100/50"
          />
        </UDropdown>
      </div>
      <CommonAiLoader :model="model" class="min-h-[550px]" />
    </div>
  </div>
</template>

<script setup lang="ts">

const { resultPage } = defineProps<{
  responseId: string;
  resultPage: ResultPage;
  model: string;
}>();

const { t } = useI18n();
const { app } = useStore();
const { simple } = useAlerts();

const rowMenu = (id: string) => [
  [{
    label: t('btn.copy'),
    icon: 'i-heroicons-document-duplicate-solid',
    click: () => {
      app.selectedAiResponseId = id;
      const responseText = JSON.stringify(resultPage.response, null, 2);
      navigator.clipboard.writeText(responseText)
        .then(() => simple.copy.success())
        .catch(() => simple.copy.error());
    }
  }, {
    label: t('sample'),
    icon: 'i-heroicons-document-plus-20-solid',
    click: () => {
      app.selectedAiResponseId = id;
      app.modal.isAddSample = true;
      app.addSampleJson = JSON.stringify(resultPage.response, null, 2);
    }
  }],
  [{
    label: t('btn.delete'),
    icon: 'i-heroicons-trash-20-solid',
    click: () => {
      app.selectedAiResponseId = id;
      app.modal.isDeleteResult = true;
    }
  }]
];

</script>
