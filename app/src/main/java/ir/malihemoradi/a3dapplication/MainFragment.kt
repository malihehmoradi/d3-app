package ir.malihemoradi.a3dapplication

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.filament.utils.HDRLoader
import dev.romainguy.kotlin.math.lookAt
import io.github.sceneview.SceneView
import io.github.sceneview.environment.loadEnvironment
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.delay

class MainFragment : Fragment(R.layout.fragment_main) {

    lateinit var sceneView: SceneView
    lateinit var loadingView: View

    var isLoading = false
        set(value) {
            field = value
            loadingView.isGone = !value
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneView = view.findViewById(R.id.sceneView)
        loadingView = view.findViewById(R.id.loadingView)

        isLoading = true

        val modelNode = ModelNode(
            position = Position(z = -4.0f),
            rotation = Rotation(x = 35.0f)
        )
        sceneView.addChild(modelNode)

        sceneView.cameraNode.transform = lookAt(
            eye = modelNode.worldPosition.let {
                Position(x = it.x, y = it.y + 0.5f, z = it.z + 2.0f)
            },
            target = modelNode.worldPosition,
            up = Direction(y = 1.0f)
        )

        lifecycleScope.launchWhenCreated {
            sceneView.environment = HDRLoader.loadEnvironment(
                context = context!!,
                lifecycle = lifecycle,
                hdrFileLocation = "environments/belfast_farmhouse_4k.hdr",
                specularFilter = false
            )?.apply {
                indirectLight?.intensity = 50_000f
            }

            modelNode.loadModelAsync(
                context = context!!,
                lifecycle = lifecycle,
                glbFileLocation = "models/fluttering_butterfly.glb",
                autoAnimate = true,
                scaleToUnits = 1.0f,
                centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
                onError = { exception -> },
                onLoaded = { modelInstance -> }
            )


            delay(1500)
            isLoading = false
            sceneView.cameraNode.smooth(
                lookAt(
                    eye = modelNode.worldPosition.let {
                        Position(x = it.x - 0.4f, y = it.y + 0.4f, z = it.z - 0.6f)
                    },
                    target = modelNode.worldPosition,
                    up = Direction(y = 1.0f)
                ),
                speed = 0.7f
            )
        }
    }
}