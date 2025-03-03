<template>
  <div class="uppy-Dashboard ">
    <Dashboard
      :uppy="uppy"
      :props="{
        metaFields: [
          { id: 'name',
            name: 'Name',
            placeholder: 'File name'
          }
        ],
        theme: uppyTheme,
        inline: true,
        height: height ?? 550,
        with: width ?? 750,
        small: true,
        autoOpen: imageEditor ? 'imageEditor' : null,
        showProgressDetails: true,
        proudlyDisplayPoweredByUppy: false,
        waitForThumbnailsBeforeUpload: false,
        showLinkToFileUploadResult: true,
        showRemoveButtonAfterComplete: true,
        hideUploadButton: hideUploadButton ? true : selectedId ? true : false
      }"
    />
  </div>
</template>

<script setup lang="ts">
import { Uppy } from '@uppy/core';
import { Dashboard } from '@uppy/vue';
import Tus from '@uppy/tus';
import ScreenCapture from '@uppy/screen-capture';
import Webcam from '@uppy/webcam';
import RemoteSources from '@uppy/remote-sources';
import ImageEditor from '@uppy/image-editor';
import Compressor from '@uppy/compressor';
import GoldenRetriever from '@uppy/golden-retriever';
import English from '@uppy/locales/lib/en_US';
import Spanish from '@uppy/locales/lib/es_ES';
import French from '@uppy/locales/lib/fr_FR';

const {
  url, auth, downURL, selectedId, samples, hideUploadButton,
  maxSize, maxFiles, localStorage
} = defineProps<{
  url?: string;
  auth?: string;
  downURL?: string;
  selectedId?: string;
  samples?: DocumentSample[];
  height?: number;
  width?: number;
  imageEditor?: boolean;
  hideUploadButton?: boolean;
  maxSize?: number;
  maxFiles?: number;
  localStorage?: boolean;
}>();

const emit = defineEmits([
  'file-added',
  'file-removed',
  'file-uploaded'
]);

const { ui, app } = useStore();
const { downloadFile, uploadFile } = useAlerts();
const api = useApi();

const uppyTheme = computed(() => ui.isDark ? 'dark' : 'light');

const uppy = new Uppy({
  id: 'uppy',
  debug: false,
  autoProceed: false,
  locale: ui.locale === 'es'
    ? Spanish
    : ui.locale === 'fr'
      ? French
      : English,
  restrictions: {
    maxFileSize: maxSize ?? 1073741824, /* default 1GB in bytes */
    maxNumberOfFiles: maxFiles ?? 12,
    minNumberOfFiles: 1,
    allowedFileTypes: [
      'image/*',
      'video/*',
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'application/vnd.ms-excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'application/vnd.ms-powerpoint',
      'application/octet-stream'
    ]
  }
}).use(Tus, {
  endpoint: url ?? api.docsURL(),
  headers: {
    Authorization: auth || '',
    'Requested-With': 'XMLHttpRequest',
    'Content-Type': 'multipart/form-data'
  }
}).use(ScreenCapture).use(Webcam, {
  showVideoSourceDropdown: true,
  showRecordingLength: true
}).use(RemoteSources as any, {
  companionUrl: 'http://companion.uppy.io',
  sources: [
    'OneDrive',
    'GoogleDrive',
    'Dropbox',
    'Box'
  ],
  companionAllowedHosts: []
} as any).use(ImageEditor).use(Compressor);

if (localStorage) {
  uppy.use(GoldenRetriever, {
    serviceWorker: true
  });
}

uppy.on('file-added', (file) => {
  if (file && file.name) {
    emit('file-added', file as unknown as UppyFile);
  }
});

uppy.on('file-removed', (file) => {
  if (file && file.name) {
    emit('file-removed', file as unknown as UppyFile);
  }
});

uppy.on('complete', (result) => {
  if (result.successful.length) {
    emit('file-uploaded', result.successful[0]);
  } else if (result.failed.length) {
    uploadFile.error();
    emit('file-uploaded', result.failed[0]);
  }
});

if (selectedId) {
  const fetchUploadedFiles = async (url: string) => {
    const { auth } = useStore();
    const res = await $fetch<DocumentDownloaded>(url, {
      headers: {
        Authorization: `${auth.type} ${auth.token}`
      },
      responseType: 'blob'
    });
    let fileType = res.type;
    if (fileType === 'application/octet-stream') {
      fileType = 'image/png';
    }
    const fileBlob = new Blob([res as BlobPart], { type: fileType });
    const file = {
      type: fileType,
      data: fileBlob,
      size: fileBlob.size
    };
    return file;
  };
  if (samples && samples.length) {
    app.isUppyLoading = true;
    const fetchPromises = samples.map(async (sample) => {
      const docId = sample.document.id;
      const fileURL = downURL ?? api.docIdDownloadURL(docId);
      const file = await fetchUploadedFiles(fileURL);
      const fileType = sample.document.name.split('.').pop();
      uppy.addFile({
        id: docId,
        name: sample.document.name,
        type: file.type ?? `application/${fileType}`,
        data: file.data,
        size: file.size,
        source: 'server',
        isRemote: false
      });
    });
    Promise.all(fetchPromises).then(() => {
      app.isUppyLoading = false;
    }).catch(() => {
      downloadFile.error();
      app.isUppyLoading = false;
    });
  }
}

</script>

<style src="@uppy/core/dist/style.css"></style>
<style src="@uppy/dashboard/dist/style.css"></style>
<style src="@uppy/image-editor/dist/style.min.css"></style>
<style src="@uppy/screen-capture/dist/style.min.css"></style>
<style src="@uppy/webcam/dist/style.min.css"></style>

<style scoped lang="scss">
.uppy-Dashboard,
.uppy-Dashboard * {
  font-family: 'Orbitron', sans-serif !important;
  border: none !important;
  color: gray !important;
  transition: color 0.3s ease;
}

.dark-mode .uppy-Dashboard,
.dark-mode .uppy-Dashboard * {
  color: white !important;
}

.light-mode .uppy-Dashboard,
.light-mode .uppy-Dashboard * {
  color: black !important;
}
</style>
