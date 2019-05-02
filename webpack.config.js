const path = require('path');

module.exports = {
    entry: './web/src/main.ts',
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'ts-loader?configFile=tsconfig.webpack.json',
                include: [path.resolve(__dirname, 'web/src')],
                exclude: [path.resolve(__dirname, 'node_modules'), path.resolve(__dirname, '_tools')]
            },
        ]
    },
    output: {
        filename: 'main.js',
        path: path.resolve(__dirname, 'src/main/resources/theme/script/')
    },
    mode: 'production',
};
