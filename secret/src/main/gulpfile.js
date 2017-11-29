// Gulp.js configuration
var
  // modules
  gulp = require('gulp'),
  newer = require('gulp-newer'),
  htmlclean = require('gulp-htmlclean'),
  concat = require('gulp-concat'),
  deporder = require('gulp-deporder'),
  stripdebug = require('gulp-strip-debug'),
  uglify = require('gulp-uglify'),
  sass = require('gulp-sass'),
  postcss = require('gulp-postcss'),
  assets = require('postcss-assets'),
  autoprefixer = require('autoprefixer'),
  mqpacker = require('css-mqpacker'),
  cssnano = require('cssnano'),

  // development mode?
  devBuild = false; //(process.env.NODE_ENV !== 'production'),

// folders
folder = {
  src: 'webSource/',
  build: 'webapp/'
}
// HTML processing
gulp.task('html', function() {
  var
    out = folder.build,
    page = gulp.src(folder.src + 'html/**/*').pipe(newer(out));

  // minify production code
  if (!devBuild) {
    page = page.pipe(htmlclean());
  }

  return page.pipe(gulp.dest(out));
});

// JavaScript processing
gulp.task('js', function() {

  var jsbuild = gulp.src(folder.src + 'js/**/*')
    .pipe(deporder())
    .pipe(concat('main.js'));

  if (!devBuild) {
    jsbuild = jsbuild
      //.pipe(stripdebug())//remove this for now, I need debug statements....
      .pipe(uglify());
  }

  return jsbuild.pipe(gulp.dest(folder.build + 'js/'));

});

gulp.task('fonts', function() {
  //just pipe through fonts, only needs to be run at start
  var
    out = folder.build + 'fonts/',
    fontbuild = gulp.src(folder.src + 'fonts/**/*').pipe(newer(out));

  return fontbuild.pipe(gulp.dest(out));
});

// CSS processing
gulp.task('css', function() {

  var postCssOpts = [
    autoprefixer({
      browsers: ['last 2 versions', '> 2%']
    }),
    mqpacker
  ];

  if (!devBuild) {
    postCssOpts.push(cssnano);
  }

  return gulp.src(folder.src + 'scss/style.scss')
    .pipe(sass({
      outputStyle: 'nested',
      imagePath: 'images/',
      precision: 3,
      errLogToConsole: true
    }))
    .pipe(postcss(postCssOpts))
    .pipe(gulp.dest(folder.build + 'css/'));

});

// watch for changes
gulp.task('watch', function() {

  // image changes
  //gulp.watch(folder.src + 'images/**/*', ['images']);

  // html changes
  gulp.watch(folder.src + 'html/**/*', ['html']);

  // javascript changes
  gulp.watch(folder.src + 'js/**/*', ['js']);

  // css changes
  gulp.watch(folder.src + 'scss/**/*', ['css']);

});


gulp.task('run', ['html', 'js', 'css', 'fonts']);


gulp.task('default', ['run', 'watch'])
