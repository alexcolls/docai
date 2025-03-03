<template>
  <CommonBtnGradient
    class="rounded-full h-fit p-0 hover:!shadow-xl shadow-black
      dark:shadow-white active:!translate-y-2 dark:!text-gray-200"
    :disabled="!app.selectedDocTypeId"
    @click="onClick"
  >
    <UIcon
      name="i-heroicons-arrow-up-circle"
      class="w-12 h-12"
    />
  </CommonBtnGradient>
</template>

<script setup lang="ts">

const { app } = useStore();
const { uploadFile } = useAlerts();

const onClick = () => {
  if (app.selectedDocTypeId) {
    app.modal.isUploadDoc = true;
  } else {
    uploadFile.missing();
    app.isMenuOpen = true;
  }
};

</script>
