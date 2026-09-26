# NutriTrack

[English](README.md) | 日本語

Kotlin と Jetpack Compose で書いた Android アプリです。患者の食事の質のデータ
(HEIFA スコア)を個人向けダッシュボードとして可視化し、食習慣を記録できるようにして、
AI が生成したアドバイスでコーチングします。別途用意した臨床医向けのビューでは、
全患者のデータを集計し、集団全体の傾向をモデルに分析させます。

制作: Haruto Iriyama

## 機能

| 画面 | 内容 |
|---|---|
| **ウェルカム / ログイン / 新規登録** | 患者データは CSV から事前に読み込まれます。ユーザーは ID と電話番号の一致で自分のアカウントを引き受け、氏名とパスワードを設定します。セッション状態は保持され、次回起動時はホーム画面から始まります。 |
| **食事摂取アンケート** | 食品カテゴリ、食習慣を最もよく表す「ペルソナ」、食事・就寝・起床の時刻を入力します。回答は Room に保存され、次回は初期値として表示されます。 |
| **ホーム** | ユーザーの総合 Food Quality Score と、その意味の簡単な説明を表示します。 |
| **インサイト** | カテゴリ別(野菜、果物、穀物、乳製品、糖分、ナトリウムなど)の HEIFA スコアを満点と比較したバーで表示し、共有ボタンも備えます。 |
| **NutriCoach** | [FruityVice](https://www.fruityvice.com/) API で任意の果物の栄養情報を検索し、ユーザー自身のスコアに合わせたモチベーション向上のアドバイスを Gemini が生成します。アドバイスは保存され、後から読み返せます。 |
| **臨床医ダッシュボード** | 臨床医キーで保護されたビュー。性別ごとの HEIFA スコア平均と、「Find data patterns」ボタンで匿名化した集団データを Gemini に送り、文章での分析を得ます。 |
| **設定** | アカウント情報、ログアウト、臨床医ビューへの入口。 |

## アーキテクチャ

- **UI** — Jetpack Compose + Material 3、ボトムナビゲーションバー。機能ごとに `ui/screens` 配下の `@Composable` 画面を 1 つ用意しています。
- **状態管理** — MVVM。各画面が `StateFlow` を公開する `ViewModel` を持ち、ViewModel が話す相手は単一の `NutriTrackRepository` だけです。
- **永続化** — Room データベース。`Patient`、`FoodIntake`、`NutriCoachTip` のエンティティと DAO を持ち、患者テーブルは初回起動時に `assets/CustomerData.csv` から投入されます。
- **通信** — FruityVice には Retrofit + Gson、コーチングと集団分析には Google Generative AI SDK(`gemini-1.5-flash`)を使用します。
- **認証** — `AuthManager` がログイン中のユーザーを `SharedPreferences` に保持します。パスワードは登録時に設定し、ログイン時に照合します。

```
app/src/main/java/com/nutritrack/
├── MainActivity.kt          NavHost とボトムバー
├── NutriTrackRepository.kt  唯一の情報源: Room + Retrofit + Gemini
├── AuthManager.kt           セッション保持、CSV ローダー
├── data/                    Room データベース、エンティティ、DAO
├── network/                 FruityVice の Retrofit サービス
├── ui/screens/              画面ごとの Composable
├── ui/theme/                Material 3 のテーマ
└── viewModel/               画面ごとの ViewModel
```

## 実行方法

Android Studio(Ladybug 以降)と、API 35 のエミュレータまたは実機が必要です。

1. プロジェクトルートに `local.properties` を作成し(git 管理外です)、Gemini の
   API キーを追加します:

   ```properties
   apiKey=YOUR_GEMINI_API_KEY
   ```

2. Android Studio でプロジェクトを開き、`app` の実行構成で起動します。

サンプルの患者データは `app/src/main/assets/CustomerData.csv` にあります。
このファイル内の任意の `User_ID` と `PhoneNumber` の組み合わせでログインできます。

臨床医ダッシュボードを開くには、**設定 → Clinician login** からデモ用キー
`dollar-entry-apples` を入力してください。これは `ClinicianLoginViewModel.kt` に
固定値として書かれたデモ用の値で、実運用向けの認証情報ではありません。
本番であればサーバー側で検証すべきものです。
