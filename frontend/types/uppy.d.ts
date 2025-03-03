declare module '@uppy/locales/lib/en_US';
declare module '@uppy/locales/lib/es_ES';
declare module '@uppy/locales/lib/fr_FR';
declare module '@uppy/image-editor';
declare module '@uppy/remote-sources' {
  import { Uppy, PluginOptions } from '@uppy/core';
  export interface RemoteSourcesOptions extends PluginOptions {
    companionUrl: string;
    sources: string[];
    companionAllowedHosts: string[];
  }
  export default class RemoteSources extends Uppy.Plugin {
    constructor(uppy: Uppy, opts: RemoteSourcesOptions);
  }
}

interface UppyFile {
  data: {
    lastModified: number;
    lastModifiedDate: Date;
    name: string;
    size: number;
    type: string;
    webkitRelativePath: string;
  };
  extension: string;
  id: string;
  isGhost: boolean;
  isRemote: boolean;
  meta: {
    relativePath: string | null;
    name: string;
    type: string;
  };
  name: string;
  preview?: string;
  progress: {
    bytesTotal: number;
    bytesUploaded: number;
    percentage: number;
    uploadComplete: boolean;
    uploadStarted: number | null;
  };
  remote: string;
  size: number;
  source: string;
  type: string;
}
