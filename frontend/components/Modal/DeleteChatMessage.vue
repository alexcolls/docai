<template>
  <UModal v-model="app.modal.isDeleteResult">
    <UCard
      :ui="{ divide: 'divide-y divide-gray-100 dark:divide-gray-800' }"
      class="!m-0 !p-0"
    >
      <template #header>
        <div class="flex items-center justify-between">
          <div class="w-5" />
          <h3
            class="font-semibold leading-6
              text-gray-900 dark:text-white"
          >
            {{ $t('deleteResponse') }}
          </h3>
          <CommonBtnClose @click="onClose" />
        </div>
      </template>
      <div class="mx-4">
        {{ $t('confirmDeleteResult') }}
      </div>
      <template #footer>
        <div class="flex justify-end">
          <CommonBtnDelete
            :loading="isLoading"
            class="mr-2"
            @click="onDelete"
          />
          <CommonBtnCancel
            class="ml-2 h-9"
            @click="onClose"
          />
        </div>
      </template>
    </UCard>
  </UModal>
</template>

<script setup lang="ts">

const { app, auth } = useStore();
const { deleteResult } = useAlerts();
const api = useApi();

const isLoading = ref(false);

const onDelete = async () => {
  if (isLoading.value) { return; }
  isLoading.value = true;
  try {
    const url = api.chatResultsURL(app.selectedAiResponseId);
    await $fetch(url, {
      method: 'DELETE',
      headers: {
        Authorization: `${auth.type} ${auth.token}`
      }
    });
    deleteResult.success();
    app.refreshChat();
  } catch (err) {
    deleteResult.error();
  }
  onClose();
  isLoading.value = false;
};

const onClose = () => {
  app.modal.isDeleteResult = false;
};

</script>
