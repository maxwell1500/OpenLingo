# Reference recordings

This folder is empty on purpose. It is the slot where a native speaker's own
voice can go, so that the review page can play a real human reading next to
the app's synthesised audio and let anyone compare the two.

The page picks files up by itself. There is nothing to configure and nothing
to rebuild: as soon as a file with the right name is in the right place,
reloading the page makes a **Reference** button appear next to that line's
Play button.

## How to add a recording

Record each line once, as a native speaker, saying it the way you would
actually say it — not a dictionary reading.

Save each file as **Vorbis `.ogg`**, the same format the app uses, and put it
here using the clip's own file name, in the folder for its language:

```
references/
  es/
    la_madre.ogg
    no_corras_con_prisa.ogg
    ...
  ja/
    matta.ogg
    issatsu.ogg
    ...
```

The file name must match exactly, including `.ogg` and lower case. The list of
names is in `../data/clips.json`, or you can see them on screen — every row
shows its clip name in small grey text above the sentence.

Any tool that records and saves `.ogg` will do: Audacity (export as Ogg Vorbis),
OBS, a phone voice memo app if it can export Ogg, or the browser itself.

## What it is for

The review page plays your recording instead of the synthesised clip, and
switches back instantly, so a reviewer can A/B them as often as they like
without losing their place. Press **Reference** twice to flip between them.

## Why it is optional

Everything above works with this folder empty. The reference slot only makes
A/B comparison possible; it is not required to review anything, and no clip is
missing a review because it has no reference.
