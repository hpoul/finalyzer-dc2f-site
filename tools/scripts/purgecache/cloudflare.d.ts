// declare module 'cloudflare' {
//
//     export = initCloudFlare;
//
//     // export default initCloudFlare;
//
//     function initCloudFlare(options: {email: string, key: string}): Cloudflare;
//
//     class Cloudflare {
//         zones: CloudflareZones;
//     }
//
//     class CloudflareZones {
//         purgeCache(zoneId: string, options: {files: string[]}): Promise<boolean>;
//     }
//
//
//
// }

declare module 'cloudflare' {
    function initCloudFlare(options: {email: string, key: string}): Cloudflare;

    class Cloudflare {
        zones: CloudflareZones;
    }

    class CloudflareZones {
        purgeCache(zoneId: string, options: {files: string[]}): Promise<boolean>;
    }

    export default initCloudFlare;
    // export = initCloudFlare;
}
// declare function cloudflare(options: {email: string, key: string}): any;

// export = cloudflare;

// export as namespace cloudflare;
//
// export = initCloudFlare;

// declare function initCloudFlare(options: {email: string, key: string}): Cloudflare;
//
// declare class Cloudflare {
//     zones: CloudflareZones;
// }
//
// declare class CloudflareZones {
//     purgeCache(zoneId: string, options: {files: string[]}): Promise<boolean>;
// }


