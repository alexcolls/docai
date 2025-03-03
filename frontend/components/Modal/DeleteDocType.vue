<template>
  <UModal v-model="app.modal.isDeleteDocType">
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
            {{ $t('btn.delete') }} {{ app.selectedDocType }}
          </h3>
          <CommonBtnClose @click="onClose" />
        </div>
      </template>
      <div class="flex cursor-pointer">
        <div class="mt-0.5">
          <UToggle v-model="isConfirmed" color="red" />
        </div>
        <div class="ml-4" @click="isConfirmed = !isConfirmed">
          {{ $t('confirmDelete') }}
        </div>
      </div>
      <p class="text-sm mt-4 mx-4 text-yellow-500 italic">
        {{ $t('deleteDescription') }}
      </p>
      <template #footer>
        <div class="flex justify-end">
          <CommonBtnDelete
            :loading="isLoading"
            :disabled="!isConfirmed"
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
const { deleteType } = useAlerts();
const api = useApi();

const isConfirmed = ref(false);
const isLoading = ref(false);

const onDelete = async () => {
  if (isLoading.value) { return; }
  isLoading.value = true;
  try {
    const url = api.docTypeIdURL(app.selectedDocTypeId);
    await $fetch(url, {
      method: 'DELETE',
      headers: {
        Authorization: `${auth.type} ${auth.token}`
      }
    });
    app.selectedDocType = '';
    app.selectedDocTypeId = '';
    await app.refreshDocTypes();
    onClose();
    deleteType.success();
    app.selectedDocTypeId = '';
  } catch (err) {
    deleteType.error();
  }
  isLoading.value = false;
};

const onClose = () => {
  app.modal.isDeleteDocType = false;
};

watch(() => app.modal.isDeleteDocType, (v) => {
  if (!v) {
    isConfirmed.value = false;
  }
});

</script>
