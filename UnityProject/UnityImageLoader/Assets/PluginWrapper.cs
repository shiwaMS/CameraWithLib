using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;
using UnityEngine.UI;

public class PluginWrapper : MonoBehaviour
{
    public RawImage display;
    public AspectRatioFitter fit;

    private Texture2D texture2D;
    private Int32 texturePtr;
    // Use this for initialization
    void Start()
    {
        this.texture2D = new Texture2D(2, 2); ;

        var plugin = new AndroidJavaClass("microsoft.prototype.broadcastsupport.ImageLoader");
        this.texturePtr = plugin.CallStatic<Int32>("loadTextureId", "/storage/emulated/0/myscreen_7.png");
        Debug.Log("texture pointer = " + this.texturePtr);
        if (texturePtr > 0)
        {
            Texture2D nativeTexture = Texture2D.CreateExternalTexture(540, 960, TextureFormat.ARGB32, false, true, new IntPtr(this.texturePtr));
            if (nativeTexture == null)
            {
                Debug.Log("nativeTexture object is null ");
                return;
            }
            else
            {
                Debug.Log("nativeTexture pixel length: " +
                        nativeTexture.GetPixels().Length);
                Debug.Log("nativeTexture width x height: " + nativeTexture.width + " x " + nativeTexture.height);
                this.texture2D.UpdateExternalTexture(nativeTexture.GetNativeTexturePtr());
                Debug.Log("texture2D width x height: " + this.texture2D.width + " x " + this.texture2D.height); // somehow it is 2x2

                display.texture = this.texture2D;

                float ratio = 540.0f / 960.0f;
                fit.aspectRatio = ratio;
                Debug.Log("texture ratio = " + ratio);
            }
        }
    }

    void Update()
    { }
}
