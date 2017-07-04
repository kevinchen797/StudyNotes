# Android录音实现（MediaRecorder）
---
最近在项目中实现录音功能，并在逻辑中还有对录音文件的特殊要求，前前后后看了很多资料，学习了很多，今天在这里分享记录一下，以便后期回看。

Android提供了两个API用于录音的实现：MediaRecorder 和AudioRecord。

1. MediaRecorder：录制的音频文件是经过压缩后的，需要设置编码器。并且录制的音频文件可以用系统自带的Music播放器播放。MediaRecorder已经集成了录音、编码、压缩等，并支持少量的录音音频格式，但是这也是他的缺点，支持的格式过少并且无法实时处理音频数据。

2. AudioRecord：主要实现对音频实时处理以及边录边播功能，相对MediaRecorder比较专业，输出是PCM语音数据，如果保存成音频文件，是不能够被播放器播放的，所以必须先写代码实现数据编码以及压缩。

### MediaRecorder

MediaRecorder因为已经集成了录音、编码、压缩等功能，所以使用起来相对比较简单。

#### 开始录音

MediaRecorder 使用起来相对简单，音频编码可以根据自己实际需要自己设定，文件名防止重复，使用了日期_时分秒的结构，audioSaveDir 是文件存储目录，可自行设定。下面贴出示例代码：

	public void startRecord() {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".m4a";
            if (!FileUtils.isFolderExist(FileUtils.getFolderName(audioSaveDir))) {
                FileUtils.makeFolders(audioSaveDir);
            }
            filePath = audioSaveDir + fileName;
            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            LogUtil.i("call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            LogUtil.i("call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

上面代码只是基本使用方式，具体使用还需结合项目具体需求制定具体逻辑，但是MediaRecorder使用时需实例化，所以在不用时一定要记得即时释放，以免造成内存泄漏。

#### 结束录音

	public void stopRecord() {
        try {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            filePath = "";
        } catch (RuntimeException e) {
            LogUtil.e(e.toString());
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

            File file = new File(filePath);
            if (file.exists())
                file.delete();

            filePath = "";
        }
    }

总结：MediaRecorder 实现录音还是比较简单的，代码量相对较少，较为简明，但也有不足之处，例如输出文件格式选择较少，录音过程不能暂停等。


