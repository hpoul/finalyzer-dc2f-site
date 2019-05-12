/// <reference path="cloudflare.d.ts" />

import * as fs from 'fs';
import * as cloudflare from 'cloudflare';

// $ export CF_API_EMAIL='user@example.com'
// $ export CF_API_KEY='00000000000000000000000000000000'

interface JsonSitemap {
    pages: [
        {
            url: string;
            lastmod: string;
        }
        ];
}

const secrets = JSON.parse(fs.readFileSync('tools/secrets/purgecache.secrets.json').toString());

const apiEmail = secrets.CF_API_EMAIL;
const apiKey = secrets.CF_API_KEY;
const apiZoneId = secrets.CF_ZONE_ID;

if (!apiEmail || !apiKey || !apiZoneId) {
    console.error('CF_API_EMAIL, CF_API_KEY and CF_ZONE_ID variables must be set in purgecache.secrets.json.');
    process.exitCode = 1;
    process.exit(1);
}

function extractUrls(jsonSitemap: string) {
    const sitemap: JsonSitemap = JSON.parse(fs.readFileSync(jsonSitemap).toString());
    return sitemap.pages.map((p) => p.url);
}

const urls = extractUrls('public/allsites.json');

console.log('clearing cache for urls: ', urls);
console.log('cloudflare', cloudflare);

const cf: Cloudflare = cloudflare({email: apiEmail, key: apiKey});
cf.zones.purgeCache(apiZoneId, { files: urls }).then((success: boolean) => {
    console.log('Successfully purged cache.', success);
}).catch((error: any) => {
    console.error('error while purging cache.', error);
    process.exit(1);
});

