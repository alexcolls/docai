<template>
  <div
    class="grid grid-cols-2 p-2 mt-4 mb-2 border border-gray-500/20 rounded-xl
      text-sm"
  >
    <div class="col-1 p-2 pt-1 border-b border-gray-500/20">
      {{ $t('name') }}
    </div>
    <div class="col-1 p-2 pt-1 border-b border-gray-500/30 overflow-x-scroll">
      {{ document.name }}
    </div>
    <div class="col-1 p-2 border-b border-gray-500/30">
      {{ $t('datetime') }}
    </div>
    <div class="col-1 p-2 border-b border-gray-500/30 overflow-x-scroll ">
      {{ formatDatetime(document.createdAt) }}
    </div>
    <div class="col-1 p-2 pt-1 border-b border-gray-500/30">
      {{ $t('tokens') }}
    </div>
    <div class="col-1 p-2 pt-1 border-b border-gray-500/30">
      {{ resultPage.tokens }}
    </div>
    <div class="col-1 p-2 border-b border-gray-500/30">
      {{ $t('responseIn') }}
    </div>
    <div class="col-1 p-2 border-b border-gray-500/30">
      {{ formatMilliseconds(resultPage.executionTime) }}
    </div>
    <div class="col-1 p-2 border-b border-gray-500/30">
      {{ $t('extension') }}
    </div>
    <div class="col-1 p-2 border-b border-gray-500/30">
      {{ resultPage.extension }}
    </div>
    <div class="col-1 p-2 pb-1">
      {{ $t('status') }}
    </div>
    <div
      class="col-1 p-2 pb-1"
      :class="resultPage.status === 'DONE' ? 'text-green-500' :
        resultPage.status === 'ERROR' ? 'text-red-500' :
        resultPage.status === 'PROCESSING' ? 'text-yellow-500' :
        'text-yellow-500'"
    >
      {{ resultPage.status ?? 'PROCESSING' }}
      <CommonLoadingDots
        v-if="resultPage.status === 'PROCESSING' || !resultPage.status"
      />
    </div>
  </div>
</template>

<script setup lang="ts">

defineProps<{
  resultPage: ResultPage;
  document: DocumentUploaded;
}>();

</script>
