package com.example.data.local

object QuranSeedData {

  fun formatAudioUrl(surah: Int, ayah: Int, reciter: String = "Husary_128kbps"): String {
    val surahFormatted = String.format("%03d", surah)
    val ayahFormatted = String.format("%03d", ayah)
    return "https://everyayah.com/data/$reciter/$surahFormatted$ayahFormatted.mp3"
  }

  val initialAyahs: List<AyahEntity> = listOf(
    // Surah Al-Fatihah (1:1 - 1:7)
    AyahEntity(
      id = 1,
      surahNumber = 1,
      surahNameArabic = "الفاتحة",
      surahNameEnglish = "Al-Fatihah",
      ayahNumberInSurah = 1,
      juzNumber = 1,
      textArabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
      textEnglish = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
      textTransliteration = "Bismillāhir-Raḥmānir-Raḥīm",
      audioUrl = formatAudioUrl(1, 1)
    ),
    AyahEntity(
      id = 2,
      surahNumber = 1,
      surahNameArabic = "الفاتحة",
      surahNameEnglish = "Al-Fatihah",
      ayahNumberInSurah = 2,
      juzNumber = 1,
      textArabic = "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَـٰلَمِينَ",
      textEnglish = "[All] praise is [due] to Allah, Lord of the worlds -",
      textTransliteration = "Al-ḥamdu lillāhi Rabbil-'ālamīn",
      audioUrl = formatAudioUrl(1, 2)
    ),
    AyahEntity(
      id = 3,
      surahNumber = 1,
      surahNameArabic = "الفاتحة",
      surahNameEnglish = "Al-Fatihah",
      ayahNumberInSurah = 3,
      juzNumber = 1,
      textArabic = "ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
      textEnglish = "The Entirely Merciful, the Especially Merciful,",
      textTransliteration = "Ar-Raḥmānir-Raḥīm",
      audioUrl = formatAudioUrl(1, 3)
    ),
    AyahEntity(
      id = 4,
      surahNumber = 1,
      surahNameArabic = "الفاتحة",
      surahNameEnglish = "Al-Fatihah",
      ayahNumberInSurah = 4,
      juzNumber = 1,
      textArabic = "مَـٰلِكِ يَوْمِ ٱلدِّينِ",
      textEnglish = "Sovereign of the Day of Recompense.",
      textTransliteration = "Māliki Yawmid-Dīn",
      audioUrl = formatAudioUrl(1, 4)
    ),
    AyahEntity(
      id = 5,
      surahNumber = 1,
      surahNameArabic = "الفاتحة",
      surahNameEnglish = "Al-Fatihah",
      ayahNumberInSurah = 5,
      juzNumber = 1,
      textArabic = "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
      textEnglish = "It is You we worship and You we ask for help.",
      textTransliteration = "Iyyāka na'budu wa iyyāka nasta'īn",
      audioUrl = formatAudioUrl(1, 5)
    ),
    AyahEntity(
      id = 6,
      surahNumber = 1,
      surahNameArabic = "الفاتحة",
      surahNameEnglish = "Al-Fatihah",
      ayahNumberInSurah = 6,
      juzNumber = 1,
      textArabic = "ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ",
      textEnglish = "Guide us to the straight path -",
      textTransliteration = "Ihdināṣ-ṣirāṭal-mustaqīm",
      audioUrl = formatAudioUrl(1, 6)
    ),
    AyahEntity(
      id = 7,
      surahNumber = 1,
      surahNameArabic = "الفاتحة",
      surahNameEnglish = "Al-Fatihah",
      ayahNumberInSurah = 7,
      juzNumber = 1,
      textArabic = "صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ",
      textEnglish = "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
      textTransliteration = "Ṣirāṭalladhīna an'amta 'alayhim ghayril-maghḍūbi 'alayhim wa laḍ-ḍāllīn",
      audioUrl = formatAudioUrl(1, 7)
    ),

    // Surah Al-Baqarah (2:1 - 2:5)
    AyahEntity(
      id = 8,
      surahNumber = 2,
      surahNameArabic = "البقرة",
      surahNameEnglish = "Al-Baqarah",
      ayahNumberInSurah = 1,
      juzNumber = 1,
      textArabic = "الٓمٓ",
      textEnglish = "Alif, Lam, Meem.",
      textTransliteration = "Alif-Lām-Mīm",
      audioUrl = formatAudioUrl(2, 1)
    ),
    AyahEntity(
      id = 9,
      surahNumber = 2,
      surahNameArabic = "البقرة",
      surahNameEnglish = "Al-Baqarah",
      ayahNumberInSurah = 2,
      juzNumber = 1,
      textArabic = "ذَٰلِكَ ٱلْكِتَـٰبُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ",
      textEnglish = "This is the Book about which there is no doubt, a guidance for those conscious of Allah -",
      textTransliteration = "Dhālikal-kitābu lā rayba fīhi hudal-lil-muttaqīn",
      audioUrl = formatAudioUrl(2, 2)
    ),
    AyahEntity(
      id = 10,
      surahNumber = 2,
      surahNameArabic = "البقرة",
      surahNameEnglish = "Al-Baqarah",
      ayahNumberInSurah = 3,
      juzNumber = 1,
      textArabic = "ٱلَّذِينَ يُؤْمِنُونَ بِٱلْغَيْبِ وَيُقِيمُونَ ٱلصَّلَوٰةَ وَمِمَّا رَزَقْنَـٰهُمْ يُنفِقُونَ",
      textEnglish = "Who believe in the unseen, establish prayer, and spend out of what We have provided for them,",
      textTransliteration = "Alladhīna yu'minūna bil-ghaybi wa yuqīmūnaṣ-ṣalāta wa mimmā razaqnāhum yunfiqūn",
      audioUrl = formatAudioUrl(2, 3)
    ),
    AyahEntity(
      id = 11,
      surahNumber = 2,
      surahNameArabic = "البقرة",
      surahNameEnglish = "Al-Baqarah",
      ayahNumberInSurah = 4,
      juzNumber = 1,
      textArabic = "وَٱلَّذِينَ يُؤْمِنُونَ بِمَآ أُنزِلَ إِلَيْكَ وَمَآ أُنزِلَ مِن قَبْلِكَ وَبِٱلْـَٔاخِرَةِ هُمْ يُوقِنُونَ",
      textEnglish = "And who believe in what has been revealed to you, [O Muhammad], and what was revealed before you, and of the Hereafter they are certain [in faith].",
      textTransliteration = "Walladhīna yu'minūna bimā unzila ilayka wa mā unzila min qablika wa bil-ākhirati hum yūqinūn",
      audioUrl = formatAudioUrl(2, 4)
    ),
    AyahEntity(
      id = 12,
      surahNumber = 2,
      surahNameArabic = "البقرة",
      surahNameEnglish = "Al-Baqarah",
      ayahNumberInSurah = 5,
      juzNumber = 1,
      textArabic = "أُو۟لَـٰٓئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ ۖ وَأُو۟لَـٰٓئِكَ هُمُ ٱلْمُفْلِحُونَ",
      textEnglish = "Those are upon [right] guidance from their Lord, and it is those who are the successful.",
      textTransliteration = "Ulā'ika 'alā hudam-mir-Rabbihim wa ulā'ika humul-mufliḥūn",
      audioUrl = formatAudioUrl(2, 5)
    ),

    // Ayat Al-Kursi (2:255) - ID: 262
    AyahEntity(
      id = 262,
      surahNumber = 2,
      surahNameArabic = "البقرة",
      surahNameEnglish = "Al-Baqarah",
      ayahNumberInSurah = 255,
      juzNumber = 3,
      textArabic = "ٱللَّهُ لَآ إِلَـٰهَ إِلَّا هُوَ ٱلْحَىُّ ٱلْقَيُّومُ ۚ لَا تَأْخُذُهُۥ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُۥ مَا فِى ٱلسَّمَـٰوَٰتِ وَمَا فِى ٱلْأَرْضِ ۗ مَن ذَا ٱلَّذِى يَشْفَعُ عِندَهُۥٓ إِلَّا بِإِذْنِهِۦ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَىْءٍ مِّنْ عِلْمِهِۦٓ إِلَّا بِمَا شَآءَ ۚ وَسِعَ كُرْسِيُّهُ ٱلسَّمَـٰوَٰتِ وَٱلْأَرْضَ ۖ وَلَا يَـُٔودُهُۥ حِفْظُهُمَا ۚ وَهُوَ ٱلْعَلِىُّ ٱلْعَظِيمُ",
      textEnglish = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is [presently] before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
      textTransliteration = "Allāhu lā ilāha illā Huwal-Ḥayyul-Qayyūm, lā ta'khudhuhū sinatuw-wa lā nawm, lahū mā fis-samāwāti wa mā fil-arḍ, man dhalladhī yashfa'u 'indahū illā bi-idhnih, ya'lamu mā bayna aydīhim wa mā khalfahum, wa lā yuḥīṭūna bishay'im-min 'ilmihī illā bimā shā', wasi'a kursiyyuhus-samāwāti wal-arḍ, wa lā ya'ūduhū ḥifẓuhumā, wa Huwal-'Aliyyul-'Aẓīm.",
      audioUrl = formatAudioUrl(2, 255)
    ),

    // Surah Al-Mulk (67:1 - 67:5)
    AyahEntity(
      id = 5242,
      surahNumber = 67,
      surahNameArabic = "الملك",
      surahNameEnglish = "Al-Mulk",
      ayahNumberInSurah = 1,
      juzNumber = 29,
      textArabic = "تَبَـٰرَكَ ٱلَّذِى بِيَدِهِ ٱلْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَىْءٍ قَدِيرٌ",
      textEnglish = "Blessed is He in whose hand is dominion, and He is over all things competent -",
      textTransliteration = "Tabārakalladhī biyadihil-mulku wa Huwa 'alā kulli shay'in Qadīr",
      audioUrl = formatAudioUrl(67, 1)
    ),
    AyahEntity(
      id = 5243,
      surahNumber = 67,
      surahNameArabic = "الملك",
      surahNameEnglish = "Al-Mulk",
      ayahNumberInSurah = 2,
      juzNumber = 29,
      textArabic = "ٱلَّذِى خَلَقَ ٱلْمَوْتَ وَٱلْحَيَوٰةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ ٱلْعَزِيزُ ٱلْغَفُورُ",
      textEnglish = "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -",
      textTransliteration = "Alladhī khalaqal-mawta wal-ḥayāta liyabluwakum ayyukum aḥsanu 'amalā, wa Huwal-'Azīzul-Ghafūr",
      audioUrl = formatAudioUrl(67, 2)
    ),
    AyahEntity(
      id = 5244,
      surahNumber = 67,
      surahNameArabic = "الملك",
      surahNameEnglish = "Al-Mulk",
      ayahNumberInSurah = 3,
      juzNumber = 29,
      textArabic = "ٱلَّذِى خَلَقَ سَبْعَ سَمَـٰوَٰتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِى خَلْقِ ٱلرَّحْمَـٰنِ مِن تَفَـٰوُتٍ ۖ فَٱرْجِعِ ٱلْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ",
      textEnglish = "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency. So return [your] vision [to the sky]; do you see any breaks?",
      textTransliteration = "Alladhī khalaqa sab'a samāwātin ṭibāqā, mā tarā fī khalqir-Raḥmāni min tafāwut, farji'il-baṣara hal tarā min fuṭūr",
      audioUrl = formatAudioUrl(67, 3)
    ),
    AyahEntity(
      id = 5245,
      surahNumber = 67,
      surahNameArabic = "الملك",
      surahNameEnglish = "Al-Mulk",
      ayahNumberInSurah = 4,
      juzNumber = 29,
      textArabic = "ثُمَّ ٱرْجِعِ ٱلْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ ٱلْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ",
      textEnglish = "Then return [your] vision twice again. [Your] vision will return to you humbled while it is fatigued.",
      textTransliteration = "Thummarji'il-baṣara karratayni yanqalib ilaykal-baṣaru khāsi'aw-wa huwa ḥasīr",
      audioUrl = formatAudioUrl(67, 4)
    ),
    AyahEntity(
      id = 5246,
      surahNumber = 67,
      surahNameArabic = "الملك",
      surahNameEnglish = "Al-Mulk",
      ayahNumberInSurah = 5,
      juzNumber = 29,
      textArabic = "وَلَقَدْ زَيَّنَّا ٱلسَّمَآءَ ٱلدُّنْيَا بِمَصَـٰبِيحَ وَجَعَلْنَـٰهَا رُجُومًا لِّلشَّيَـٰطِينِ ۖ وَأَعْتَدْنَا لَهُمْ عَذَابَ ٱلسَّعِيرِ",
      textEnglish = "And We have certainly beautified the nearest heaven with lamps and have made [from] them what is thrown at the devils and have prepared for them the punishment of the Blaze.",
      textTransliteration = "Wa laqad zayyannās-samā'ad-dunyā bimaṣābīḥa wa ja'alnāhā rujūmal-lish-shayāṭīn, wa a'tadnā lahum 'adhābas-sa'īr",
      audioUrl = formatAudioUrl(67, 5)
    ),

    // Surah An-Naba (78:1 - 78:5)
    AyahEntity(
      id = 5673,
      surahNumber = 78,
      surahNameArabic = "النبأ",
      surahNameEnglish = "An-Naba",
      ayahNumberInSurah = 1,
      juzNumber = 30,
      textArabic = "عَمَّ يَتَسَآءَلُونَ",
      textEnglish = "About what are they asking one another?",
      textTransliteration = "'Amma yatasā'alūn",
      audioUrl = formatAudioUrl(78, 1)
    ),
    AyahEntity(
      id = 5674,
      surahNumber = 78,
      surahNameArabic = "النبأ",
      surahNameEnglish = "An-Naba",
      ayahNumberInSurah = 2,
      juzNumber = 30,
      textArabic = "عَنِ ٱلنَّبَإِ ٱلْعَظِيمِ",
      textEnglish = "About the great news -",
      textTransliteration = "'Anin-naba'il-'aẓīm",
      audioUrl = formatAudioUrl(78, 2)
    ),
    AyahEntity(
      id = 5675,
      surahNumber = 78,
      surahNameArabic = "النبأ",
      surahNameEnglish = "An-Naba",
      ayahNumberInSurah = 3,
      juzNumber = 30,
      textArabic = "ٱلَّذِى هُمْ فِيهِ مُخْتَلِفُونَ",
      textEnglish = "That over which they are in disagreement.",
      textTransliteration = "Alladhī hum fīhi mukhtalifūn",
      audioUrl = formatAudioUrl(78, 3)
    ),
    AyahEntity(
      id = 5676,
      surahNumber = 78,
      surahNameArabic = "النبأ",
      surahNameEnglish = "An-Naba",
      ayahNumberInSurah = 4,
      juzNumber = 30,
      textArabic = "كَلَّا سَيَعْلَمُونَ",
      textEnglish = "No! They are going to know.",
      textTransliteration = "Kallā saya'lamūn",
      audioUrl = formatAudioUrl(78, 4)
    ),
    AyahEntity(
      id = 5677,
      surahNumber = 78,
      surahNameArabic = "النبأ",
      surahNameEnglish = "An-Naba",
      ayahNumberInSurah = 5,
      juzNumber = 30,
      textArabic = "ثُمَّ كَلَّا سَيَعْلَمُونَ",
      textEnglish = "Then, no! They are going to know.",
      textTransliteration = "Thumma kallā saya'lamūn",
      audioUrl = formatAudioUrl(78, 5)
    ),

    // Surah Al-Asr (103:1 - 103:3) - Complete
    AyahEntity(
      id = 6177,
      surahNumber = 103,
      surahNameArabic = "العصر",
      surahNameEnglish = "Al-'Asr",
      ayahNumberInSurah = 1,
      juzNumber = 30,
      textArabic = "وَٱلْعَصْرِ",
      textEnglish = "By time,",
      textTransliteration = "Wal-'aṣr",
      audioUrl = formatAudioUrl(103, 1)
    ),
    AyahEntity(
      id = 6178,
      surahNumber = 103,
      surahNameArabic = "العصر",
      surahNameEnglish = "Al-'Asr",
      ayahNumberInSurah = 2,
      juzNumber = 30,
      textArabic = "إِنَّ ٱلْإِنسَـٰنَ لَفِى خُسْرٍ",
      textEnglish = "Indeed, mankind is in loss,",
      textTransliteration = "Innal-insāna lafī khusr",
      audioUrl = formatAudioUrl(103, 2)
    ),
    AyahEntity(
      id = 6179,
      surahNumber = 103,
      surahNameArabic = "العصر",
      surahNameEnglish = "Al-'Asr",
      ayahNumberInSurah = 3,
      juzNumber = 30,
      textArabic = "إِلَّا ٱلَّذِينَ ءَامَنُوا۟ وَعَمِلُوا۟ ٱلصَّـٰلِحَـٰتِ وَتَوَاصَوْا۟ بِٱلْحَقِّ وَتَوَاصَوْا۟ بِٱلصَّبْرِ",
      textEnglish = "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.",
      textTransliteration = "Illalladhīna āmanū wa 'amiluṣ-ṣāliḥāti wa tawāṣaw bil-ḥaqqi wa tawāṣaw biṣ-ṣabr",
      audioUrl = formatAudioUrl(103, 3)
    ),

    // Surah Al-Kawthar (108:1 - 108:3) - Complete
    AyahEntity(
      id = 6205,
      surahNumber = 108,
      surahNameArabic = "الكوثر",
      surahNameEnglish = "Al-Kawthar",
      ayahNumberInSurah = 1,
      juzNumber = 30,
      textArabic = "إِنَّآ أَعْطَيْنَـٰكَ ٱلْكَوْثَرَ",
      textEnglish = "Indeed, We have granted you, [O Muhammad], al-Kawthar.",
      textTransliteration = "Innā a'ṭaynākal-Kawthar",
      audioUrl = formatAudioUrl(108, 1)
    ),
    AyahEntity(
      id = 6206,
      surahNumber = 108,
      surahNameArabic = "الكوثر",
      surahNameEnglish = "Al-Kawthar",
      ayahNumberInSurah = 2,
      juzNumber = 30,
      textArabic = "فَصَلِّ لِرَبِّكَ وَٱنْحَرْ",
      textEnglish = "So pray to your Lord and sacrifice [to Him alone].",
      textTransliteration = "Faṣalli li-Rabbika wanḥar",
      audioUrl = formatAudioUrl(108, 2)
    ),
    AyahEntity(
      id = 6207,
      surahNumber = 108,
      surahNameArabic = "الكوثر",
      surahNameEnglish = "Al-Kawthar",
      ayahNumberInSurah = 3,
      juzNumber = 30,
      textArabic = "إِنَّ شَانِئَكَ هُوَ ٱلْأَبْتَرُ",
      textEnglish = "Indeed, your enemy is the one cut off.",
      textTransliteration = "Inna shāni'aka huwal-abtar",
      audioUrl = formatAudioUrl(108, 3)
    ),

    // Surah Al-Ikhlas (112:1 - 112:4) - Complete
    AyahEntity(
      id = 6222,
      surahNumber = 112,
      surahNameArabic = "الإخلاص",
      surahNameEnglish = "Al-Ikhlas",
      ayahNumberInSurah = 1,
      juzNumber = 30,
      textArabic = "قُلْ هُوَ ٱللَّهُ أَحَدٌ",
      textEnglish = "Say, \"He is Allah, [who is] One,",
      textTransliteration = "Qul Huwal-Lāhu Aḥad",
      audioUrl = formatAudioUrl(112, 1)
    ),
    AyahEntity(
      id = 6223,
      surahNumber = 112,
      surahNameArabic = "الإخلاص",
      surahNameEnglish = "Al-Ikhlas",
      ayahNumberInSurah = 2,
      juzNumber = 30,
      textArabic = "ٱللَّهُ ٱلصَّمَدُ",
      textEnglish = "Allah, the Eternal Refuge.",
      textTransliteration = "Allāhuṣ-Ṣamad",
      audioUrl = formatAudioUrl(112, 2)
    ),
    AyahEntity(
      id = 6224,
      surahNumber = 112,
      surahNameArabic = "الإخلاص",
      surahNameEnglish = "Al-Ikhlas",
      ayahNumberInSurah = 3,
      juzNumber = 30,
      textArabic = "لَمْ يَلِدْ وَلَمْ يُولَدْ",
      textEnglish = "He neither begets nor is born,",
      textTransliteration = "Lam yalid wa lam yūlad",
      audioUrl = formatAudioUrl(112, 3)
    ),
    AyahEntity(
      id = 6225,
      surahNumber = 112,
      surahNameArabic = "الإخلاص",
      surahNameEnglish = "Al-Ikhlas",
      ayahNumberInSurah = 4,
      juzNumber = 30,
      textArabic = "وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌۢ",
      textEnglish = "Nor is there to Him any equivalent.\"",
      textTransliteration = "Wa lam yakul-lahū kufuwan aḥad",
      audioUrl = formatAudioUrl(112, 4)
    ),

    // Surah Al-Falaq (113:1 - 113:5) - Complete
    AyahEntity(
      id = 6226,
      surahNumber = 113,
      surahNameArabic = "الفلق",
      surahNameEnglish = "Al-Falaq",
      ayahNumberInSurah = 1,
      juzNumber = 30,
      textArabic = "قُلْ أَعُوذُ بِرَبِّ ٱلْفَلَقِ",
      textEnglish = "Say, \"I seek refuge in the Lord of daybreak",
      textTransliteration = "Qul a'ūdhu bi-Rabbil-falaq",
      audioUrl = formatAudioUrl(113, 1)
    ),
    AyahEntity(
      id = 6227,
      surahNumber = 113,
      surahNameArabic = "الفلق",
      surahNameEnglish = "Al-Falaq",
      ayahNumberInSurah = 2,
      juzNumber = 30,
      textArabic = "مِن شَرِّ مَا خَلَقَ",
      textEnglish = "From the evil of that which He created",
      textTransliteration = "Min sharri mā khalaq",
      audioUrl = formatAudioUrl(113, 2)
    ),
    AyahEntity(
      id = 6228,
      surahNumber = 113,
      surahNameArabic = "الفلق",
      surahNameEnglish = "Al-Falaq",
      ayahNumberInSurah = 3,
      juzNumber = 30,
      textArabic = "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ",
      textEnglish = "And from the evil of darkness when it settles",
      textTransliteration = "Wa min sharri ghāsiqin idhā waqab",
      audioUrl = formatAudioUrl(113, 3)
    ),
    AyahEntity(
      id = 6229,
      surahNumber = 113,
      surahNameArabic = "الفلق",
      surahNameEnglish = "Al-Falaq",
      ayahNumberInSurah = 4,
      juzNumber = 30,
      textArabic = "وَمِن شَرِّ ٱلنَّفَّـٰثَـٰتِ فِى ٱلْعُقَدِ",
      textEnglish = "And from the evil of the blowers in knots",
      textTransliteration = "Wa min sharrin-naffāthāti fil-'uqad",
      audioUrl = formatAudioUrl(113, 4)
    ),
    AyahEntity(
      id = 6230,
      surahNumber = 113,
      surahNameArabic = "الفلق",
      surahNameEnglish = "Al-Falaq",
      ayahNumberInSurah = 5,
      juzNumber = 30,
      textArabic = "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
      textEnglish = "And from the evil of an envier when he envies.\"",
      textTransliteration = "Wa min sharri ḥāsidin idhā ḥasad",
      audioUrl = formatAudioUrl(113, 5)
    ),

    // Surah An-Nas (114:1 - 114:6) - Complete
    AyahEntity(
      id = 6231,
      surahNumber = 114,
      surahNameArabic = "الناس",
      surahNameEnglish = "An-Nas",
      ayahNumberInSurah = 1,
      juzNumber = 30,
      textArabic = "قُلْ أَعُوذُ بِرَبِّ ٱلنَّاسِ",
      textEnglish = "Say, \"I seek refuge in the Lord of mankind,",
      textTransliteration = "Qul a'ūdhu bi-Rabbin-nās",
      audioUrl = formatAudioUrl(114, 1)
    ),
    AyahEntity(
      id = 6232,
      surahNumber = 114,
      surahNameArabic = "الناس",
      surahNameEnglish = "An-Nas",
      ayahNumberInSurah = 2,
      juzNumber = 30,
      textArabic = "مَلِكِ ٱلنَّاسِ",
      textEnglish = "The Sovereign of mankind,",
      textTransliteration = "Malikin-nās",
      audioUrl = formatAudioUrl(114, 2)
    ),
    AyahEntity(
      id = 6233,
      surahNumber = 114,
      surahNameArabic = "الناس",
      surahNameEnglish = "An-Nas",
      ayahNumberInSurah = 3,
      juzNumber = 30,
      textArabic = "إِلَـٰهِ ٱلنَّاسِ",
      textEnglish = "The God of mankind,",
      textTransliteration = "Ilāhin-nās",
      audioUrl = formatAudioUrl(114, 3)
    ),
    AyahEntity(
      id = 6234,
      surahNumber = 114,
      surahNameArabic = "الناس",
      surahNameEnglish = "An-Nas",
      ayahNumberInSurah = 4,
      juzNumber = 30,
      textArabic = "مِن شَرِّ ٱلْوَسْوَاسِ ٱلْخَنَّاسِ",
      textEnglish = "From the evil of the retreating whisperer -",
      textTransliteration = "Min sharril-waswāsil-khannās",
      audioUrl = formatAudioUrl(114, 4)
    ),
    AyahEntity(
      id = 6235,
      surahNumber = 114,
      surahNameArabic = "الناس",
      surahNameEnglish = "An-Nas",
      ayahNumberInSurah = 5,
      juzNumber = 30,
      textArabic = "ٱلَّذِى يُوَسْوِسُ فِى صُدُورِ ٱلنَّاسِ",
      textEnglish = "Who whispers into the breasts of mankind -",
      textTransliteration = "Alladhī yuwaswisu fī ṣudūrin-nās",
      audioUrl = formatAudioUrl(114, 5)
    ),
    AyahEntity(
      id = 6236,
      surahNumber = 114,
      surahNameArabic = "الناس",
      surahNameEnglish = "An-Nas",
      ayahNumberInSurah = 6,
      juzNumber = 30,
      textArabic = "مِنَ ٱلْجِنَّةِ وَٱلنَّاسِ",
      textEnglish = "From among the jinn and mankind.\"",
      textTransliteration = "Minal-jinnati wan-nās",
      audioUrl = formatAudioUrl(114, 6)
    )
  )
}
